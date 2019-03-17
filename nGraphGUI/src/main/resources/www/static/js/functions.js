/********************/
/* JQuery UI Layout */
/********************/
// Set Panes properties
function setUILayoutsProperties() {
	$('body').layout(UI_Layout_properties);
}

/***************************************/
/* Left pane Entities    functions */
/***************************************/
// Show Entities count with commas
function numberWithCommas(x) {
    var parts = x.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

// Update label badge & Style with visible entities
function Update_Labels() {
  stats = {}
  cy.elements().forEach(function (ele){
  	if (ele.visible()) {
	  	data = ele.data()
		  stats[data.type] = ++stats[data.type] || 1;
	  }
  });
  var elPropsE = document.getElementById('Entities');
  var elPropsR = document.getElementById('Relationships');
  elPropsE.innerHTML = '';
  elPropsR.innerHTML = '';
  for (var key in stats) {
    if (/[a-z]/.test(key)) {
      elItem = document.createElement('li');
      elItem.setAttribute('class', 'list-group-item list-group-item-' + key.toLowerCase());
      elItem.innerHTML = ':' + key.charAt(0).toUpperCase() + key.slice(1) + ' <span class="badge badge-light">' + stats[key] + '</span>';
      elItem.onclick = function () {
          tag = this.innerText.substring(1, this.innerText.length).split("\n")[0].split(" ")[0].trim();
          cy.nodes(":selected").deselect();
          cy.nodes("[type = '" + tag + "']").select();
      };
      elPropsE.insertBefore(elItem, elPropsE.firstChild);
    } else {
      elItem = document.createElement('li');
      elItem.setAttribute('class', 'list-group-item list-group-item-edge');
      elItem.innerHTML = ':' + key.charAt(0).toUpperCase() + key.slice(1) + ' <span class="badge badge-light">' + stats[key] + '</span>';
      elItem.onclick = function () {
          tag = this.innerText.substring(1, this.innerText.length).split("\n")[0].split(" ")[0].trim();
          cy.nodes(":selected").deselect();
          rels = cy.edges("[type = '" + tag + "']");
          for (var i = 0; i<rels.length; i++) {
            cy.nodes("[id = '" + rels[i].data().source + "']").select();
            cy.nodes("[id = '" + rels[i].data().target + "']").select();
          }
      };
      elPropsR.insertBefore(elItem, elPropsR.firstChild);
    }
  }
}

// Update total entities labels
function Update_Totals(total_nodes, total_edges) {
  tnod = numberWithCommas(total_nodes);
  tedg = numberWithCommas(total_edges);
  var elProps = document.getElementById('Totals');
  elProps.innerHTML = '';
  elItem1 = document.createElement('li');
  elItem1.setAttribute('class', 'list-group-item list-group-item-total-nodes');
  elItem1.innerHTML = ':DB Entities <span class="badge badge-pill badge-light">' + tnod + '</span>';
  elProps.insertBefore(elItem1, elProps.firstChild);

  elItem2 = document.createElement('li');
  elItem2.setAttribute('class', 'list-group-item list-group-item-total-edges');
  elItem2.innerHTML = ':DB Relationships <span class="badge badge-pill badge-light">' + tedg + '</span>';
  elProps.insertBefore(elItem2, elProps.firstChild);

}

/*************************/
/* Cytoscape graph utils */
/*************************/
// setupCyGraph
function setupCyGraph() {
  cy = window.cy = setCyProperties();
  cy_panzoom = setupCyPanZoom();
  BindNodeClick();
  var cy_context = setupCyContextMenus();
  graphAutoResize();
}

// Set Cytoscape properties
function setCyProperties() {
  return cytoscape({
    container: $('#cy-graph'),
    elements: results,
    //layout: last_layout,
    layout : {
        name : last_layout["name"],
        animate: true,
        padding: 10
    },
    style: CyGraphStyle
  });
}

// Setup cytoscape pan zoom plugin properties
function setupCyPanZoom() {
  return cy.panzoom({ 
      container: $('#cy-graph')
  });
}

// Setup cytoscape contex menu plugin properties
function setupCyContextMenus() {
  return cy.contextMenus({
    menuItems: [
      {
        id: 'hide_selected',
        content: 'Hide selected nodes',
        selector: 'node, edge',
        onClickFunction: function (event) {
          cy.elements(":selected").hide();
          restore_layout();
          Update_Labels(); 
        }
      },
      {
        id: 'hide_nonselected',
        content: 'Hide non-selected nodes',
        selector: 'node, edge',
        onClickFunction: function (event) {
          cy.elements(":unselected").hide();
          restore_layout();
          Update_Labels(); 
        }
      },
      {
        id: 'show_all',
        content: 'Show all nodes',
        selector: 'node, edge',
        onClickFunction: function (event) {
          cy.elements().show();
          restore_layout();
          Update_Labels();
        },
        hasTrailingDivider: true
      },
      {
        id: 'select_all',
        content: 'Select all nodes',
        selector: 'node',
        onClickFunction: function (event) {
          cy.elements().select();
        },
      },
      {
        id: 'deselect_all',
        content: 'Deselect all nodes',
        selector: 'node',
        onClickFunction: function (event) {
          cy.elements(":selected").deselect();
        }
      },
      {
        id: 'select_neighborhood',
        content: 'Select neighborhood',
        selector: 'node',
        onClickFunction: function (event) {
          cy.elements(":selected").forEach(function (ele){
            if (ele.visible()) {
              ele.neighborhood().select()
            }
          });
        }                
      },
      {
        id: 'invert_selection',
        content: 'Invert selection',
        selector: 'node',
        onClickFunction: function (event) {
          var sel = cy.elements(":selected");
          var unsel = cy.elements(":unselected");
          sel.deselect();
          unsel.select();
        },
        hasTrailingDivider: true
      },

      {
        id: 'inspect_selection',
        content: 'Inspect selection Properties',
        selector: 'node',
        onClickFunction: function (event) {
          var data = []
          cy.elements(":selected").forEach(function (ele){
            if (ele.visible()) {
              data.push(ele.data());
            }
          });
          editor_props.setValue( js_beautify( JSON.stringify( data ) ), -1 );
        }
      },
      {
        id: 'fit_selection',
        content: 'Fit selection',
        selector: 'node',
        onClickFunction: function (event) {
          cy.fit( cy.$(':selected'), 50 );
        }
      },
      {
        id: 'expand_selection',
        content: 'Expand selection neighborhood',
        selector: 'node',
        onClickFunction: function (event) {
          var ids = [];
          cy.elements(":selected").forEach(function (ele){
            if (ele.visible()) {
              ids.push(ele.data().id);
            }
          });

          $.ajax({
              type: 'POST',
              url: 'http://127.0.0.1:8080/graph/expand/',
              contentType: 'application/json',
              data: JSON.stringify( {"id":ids} ),
              success: function(data){
                graphUpdateData(data.nodes, data.rels);
                Update_Labels(); 
              }
          })
        },
        hasTrailingDivider: true
      },

      {
        id: 'show_source_code',
        content: 'Show source Code',
        selector: 'node',
        onClickFunction: function (event) { 
          $.ajax({
              type: 'POST',
              url: 'http://127.0.0.1:8080/graph/code/',
              contentType: 'application/json',
              data: JSON.stringify(event.target.data(), null, '\t'),
              success: function(data){ 
                editor_code.setValue(data["code"], -1);
              }
          });
        }
      }
    ]
  });
}

// Bind graph nodes click event
function BindNodeClick() {
  cy.on('click', 'node', function(e) {
    editor_props.setValue(
        js_beautify(
            JSON.stringify(
                e.target.data()
            )
        )
        ,-1
    );
  });
}

// Auto resize graph/editors with pane size changes
function graphAutoResize() {
  var e2 = document.getElementById('graph_pane');
  new ResizeSensor(e2, function() {
      cy.resize();
      editor_code.resize();
      editor_query.resize();
      editor_props.resize();
  })
}

// Update graph with new data
function graphUpdateData(nodes, rels) {
      cy.style().resetToDefault();
      cy.add({"edges":rels, "nodes": nodes});
      cy.style(CyGraphStyle);
      var l = cy.layout(last_layout);
      l.run();
}

// Change graph layout
function change_CyLayout(layout) {
  switch (layout) {
    case "BF":
      last_layout = CyLayouts[0];
      break;
    case "DG":
      last_layout = CyLayouts[1];
      break;
    case "CC":
      last_layout = CyLayouts[2];
      break;
    case "KL":
      last_layout = CyLayouts[3];
      break;
    case "CO":
      last_layout = CyLayouts[4];
      break;
    default:
      last_layout = CyLayouts[0];
      break;
  }
  nl = cy.layout(last_layout);
  nl.run();
}

// Clear graph data
function clear_CyLayout() {
  cy.elements().remove();
  nodes = [], rels = [];
  $.ajax({
      type: 'GET',
      url: 'http://127.0.0.1:8080/graph/reset/',
      contentType: 'application/json',
      success: function(data){ 
      }
  })
  Update_Labels();
}

// Set graph layout
function restore_layout() {
    nl = cy.layout(last_layout);
    nl.run();
};

/**********************/
/* Neo4J Driver utils */
/**********************/
// Connect to neo4j instance
function neo4jConnect() {
  $.ajax({
      type: 'GET',
      url: 'http://127.0.0.1:8080/graph/totals/',
      contentType: 'application/json',
      success: function(data){ 
        Update_Totals(data.nodes , data.rels);
      }
  })
}

function neo4jCypherQuery(query) {
  $.ajax({
      type: 'POST',
      url: 'http://127.0.0.1:8080/graph/query/',
      contentType: 'application/json',
      data: JSON.stringify( {"query": query} ),
      success: function(data){
        graphUpdateData(data.nodes, data.rels);
        Update_Labels();
        if (data.status != "") {
            $('#alert_placeholder').html('<div class="alert alert-danger"><span>'+data.status+'</span></div>');
            setTimeout(function() {
                $(".alert").remove();
            }, 5000);

        }

        console.log(data.status);
      }
  });
}

/*************/
/* Tabs Pane */
/*************/

// Create and set up initials tabs properties
function setupTabsPane() {
  tabs = $( "#tabs_pane" ).tabs({ active: 0 });
  tabs.find( ".ui-tabs-nav" ).sortable({
    axis: "x",
    stop: function() {
      tabs.tabs( "refresh" );
    }
  });  
}

// Bind options clicks
function bindOptionsClick() {
  $(".button_invoke").on('click', function(e){ 
    neo4jCypherQuery( editor_query.getValue() );
  });                            

  $("#cyClear").on('click', function(e){ 
    clear_CyLayout();
  });

  $("#cyLayout_BF, #cyLayout_DG, #cyLayout_CC, #cyLayout_KL, #cyLayout_CO").on('click', function(e){ 
    change_CyLayout( e.target.id.split("_")[1] );
  });

  $("#cyDownload_PNG, #cyDownload_JPG").on("click", function(e){
    dtype = e.target.id.split("_")[1];
    switch (dtype) {
        case "PNG":
            var text = window.cy.png({'output': 'blob'});
            var name = e.target.id + ".png";
            break;
        case "JPG":
            var text = window.cy.jpg({'output': 'blob'});
            var name = e.target.id + ".jpg";
            break;
    }
    var a = document.getElementById(e.target.id);
    var file = new Blob([text], { type: text.type });
    a.href = URL.createObjectURL(file);
    a.download = name;
    a.click();
  });

  var staticWordCompleter = {
    getCompletions: function(editor, session, pos, prefix, callback) {
        var wordList;
        var meta_desc;
        var key;
        // Common phrase tokens
        var methods = ["method", "entrypoint", "activity", "service", "provider", "receiver"];
        var entities  = methods.concat(["class", "parameter", "field", "literal"]);
        var relations = ["extending", "implementing", "invoking", "returning", "having", "reading", "writing", "using"];
        var directions = ["from", "to"];
        var comparision = ["equals", "like"];
        var operators = ["or", "and"];
        // Entity properties & Stages
        var properties = {
            "class": ["id", "name"],
            "method": ["id", "class", "name", "descriptor"],
            "parameter": ["id", "class"],
            "field": ["id", "class", "name", "field_type"],
            "literal": ["id", "value"]
        };
        var entity_actions = {
            "class": ["extending", "implementing"],
            "method": ["invoking", "returning", "having", "reading", "writing", "using"],
            "entrypoint": ["invoking", "returning", "having", "reading", "writing", "using"],
            "activity": ["invoking", "returning", "having", "reading", "writing", "using"],
            "service": ["invoking", "returning", "having", "reading", "writing", "using"],
            "provider": ["invoking", "returning", "having", "reading", "writing", "using"],
            "receiver": ["invoking", "returning", "having", "reading", "writing", "using"]
        }
        var verb_direction = {
            "extending": ["class"],
            "implementing": methods,
            "invoking": methods,
            "returning": ["parameter"],
            "having": ["parameter"],
            "reading": ["field"],
            "writing": ["field"],
            "using": ["literal"]
        }
        var phrase_begins = entities.concat(["neighbours of", "path from", "references"]);

        // query line tokens
        var curLine = session.getDocument().getLine(pos.row);
        var curTokens = curLine.slice(0, pos.column).split(/\s+/);
        var curCmd = curTokens[curTokens.length - 2];

        // tokens analysis
        if (curTokens.length == 1) {
            wordList = phrase_begins;
            meta_desc = "PHRASE BEGINS";
        } else if (entities.includes(curCmd.toLowerCase())) {
            key = curTokens[curTokens.length - 2];
            console.log(key);
            if (!Object.keys(entity_actions).includes(key)) {
                wordList = ["with"];
            } else {
                wordList = entity_actions[key.toLowerCase()].concat("with");
            }
            meta_desc = "AFTER ENTITY";
        } else if (relations.includes(curCmd.toLowerCase())) {
            key = curTokens[curTokens.length - 2];
            wordList = verb_direction[key.toLowerCase()].concat("common");
            meta_desc = "AFTER VERB";
        } else if (curCmd.toLowerCase() == "common") {
            wordList = entities;
            meta_desc = "ENTITY";
        } else if (curCmd.toLowerCase() == "of") {
            wordList = entities;
            meta_desc = "ENTITY";
        } else if (curCmd.toLowerCase() == "references") {
            wordList = directions;
            meta_desc = "DIRECTION";
        } else if (directions.includes(curCmd.toLowerCase())) {
            wordList = entities;
            meta_desc = "ENTITY";
        } else if (curCmd.toLowerCase() == "with") {
            // Commons
            if (curTokens.length > 3) {
                // Commons
                if ((curTokens[2].toLowerCase() == "common") && (entities.includes(curTokens[3].toLowerCase()))) {
                    wordList = entities;
                    meta_desc = "ENTITY";
                // Filter
                } else {
                    var key = curTokens[curTokens.length - 3];
                    if (methods.includes(key.toLowerCase())) {
                        wordList = properties["method"];
                    } else {
                        wordList = properties[curTokens[curTokens.length - 3].toLowerCase()]
                    }
                    meta_desc = "PROPERTY";
                }
            // Filter
            } else {
                var key = curTokens[curTokens.length - 3];
                if (methods.includes(key.toLowerCase())) {
                    wordList = properties["method"];
                } else {
                    wordList = properties[curTokens[curTokens.length - 3].toLowerCase()]
                }
                meta_desc = "PROPERTY";
            }
        } else {
            wordList = comparision;
            meta_desc = "OPERATOR";
        }

        callback(null, wordList.map(function(word) {
            return {
                caption: word,
                value: word,
                meta: meta_desc
            };
        }));
    }
  }

  $('#language').change(function() {
    editor_query.setValue("");
    // Natural
    if (!$(this).prop('checked')) {
      editor_query.setTheme("ace/theme/kuroir");
      editor_query.getSession().setMode("ace/mode/asciidoc");
      editor_query.setOptions({
        enableBasicAutocompletion: true,
        enableLiveAutocompletion: true
      });
      editor_query.completers = [staticWordCompleter]
    // Cypher
    } else {
      editor_query.setTheme("ace/theme/tomorrow");
      editor_query.getSession().setMode("ace/mode/sql");
      editor_query.completers = []
    }
  });

}


/**************/
/* ACE Editor */
/**************/

// Setup ACE theme & language
function setupACEditor() {
  langTools = ace.require('ace/ext/language_tools');
  editor_query = ace.edit('editor_query');
  editor_query.setTheme("ace/theme/tomorrow");
  editor_query.getSession().setMode("ace/mode/sql");
  editor_query.renderer.setOption('showLineNumbers', false);

  editor_code = ace.edit('editor_code');
  editor_code.setTheme("ace/theme/clouds");
  editor_code.getSession().setMode("ace/mode/java");
  editor_code.setOptions({readOnly: true});

  editor_props = ace.edit('editor_props');
  editor_props.setTheme("ace/theme/clouds");
  editor_props.getSession().setMode("ace/mode/json");
  editor_props.setOptions({readOnly: true});
  editor_props.renderer.setOption('showLineNumbers', false);
}