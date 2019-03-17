

/*******************/
// JQuery UI Layouts 
var UI_Layout_properties = {
  spacing_open: 3,
  south__size: 30,
  south__closable: false,
  south__resizable: false,
  south__slidable: false,
  south__spacing_open: 0,
  center__childOptions : {
      west__spacing_open: 2,
      west__spacing_closed: 7,
      west__size: 265,
	  west__resizable: false,
      center__childOptions : {
          south__size: .20,
          south__spacing_open: 2,
          south__spacing_closed: 7,
          south__minSize: 160
      }
  }
};

/**************************/
// Cytoscape elements Style
var CyDomainStyle = {
    'global': {
        'border-width': 1,
        'text-valign': "center",
        'text-halign': "center",
        'text-wrap': "ellipsis",
        'text-max-width': "48px",
        //'text-transform': 'uppercase',
        
        'color': '#ffffff',
        'padding-left': 10,
        'padding-top': 10,
        'font-size': '12px',
        'shape': 'ellipse',
        'background-opacity': 1,
        'border-color': '#fff',
        'border-width': 0

    },
    'class': {
        'label': 'data(name)',
        'background-color': '#00c5e1'
    },
    'method': {
        'label': 'data(name)',
        'background-color': '#fe5058'
    },
    'entrypoint': {
        //'label': 'data(name)',
        'background-color': '#fe8855'
    },
    'activity': {
        'label': 'data(name)',
        'background-color': '#ffbd35'
    },
    'service': {
        'label': 'data(name)',
        'background-color': '#ffe15b'
    },
    'provider': {
        'label': 'data(name)',
        'background-color': '#bcdd5a'
    },
    'receiver': {
        'label': 'data(name)',
        'background-color': '#6ec985'
    },
    'field': {
        'label': 'data(name)',
        'background-color': '#8da6d6'
    },
    'string': {
        'label': 'data(value)',
        'background-color': '#cd81b9'
    },
    'parameter': {
        'label': 'data(class)',
        'background-color': '#27c1b3'
    },
    'global_edge': {
        'curve-style': 'bezier',
        'width': 2,
        'target-arrow-shape': 'triangle',
        //'source-arrow-shape': 'circle',
        'label': 'data(type)',
        'text-rotation': 'autorotate',
        'text-background-opacity': 1,
        'text-background-color': '#eeeeee',
        'text-background-opacity': 1,
        'font-size': '12px',
        'line-color': "#a5abb6",
        'target-arrow-color': "#a5abb6",
        //'color': '#a5abb6'
        'color': '#757b86'
    },
    'active_node': {
      'border-width': '4px',
      'border-color': '#000',
      'background-opacity': 1
    }
}

/*****************************/
// Cytoscape Layouts properties
var CyLayouts = [
  { name: 'breadthfirst'},
  { name: 'dagre'},
  { name: 'circle' },
  { name: 'klay'},
  { name: 'cose'}
];

/*******************************/
// Cytoscape Graph style setters
var CyGraphStyle = [
  { selector: 'node', style: CyDomainStyle.global },
  { selector: 'node[type = "Class"]', style: CyDomainStyle.class },
  { selector: 'node[type = "Method"]', style: CyDomainStyle.method },
  { selector: 'node[type = "EntryPoint"]', style: CyDomainStyle.entrypoint },
  { selector: 'node[type = "Activity"]', style: CyDomainStyle.activity },
  { selector: 'node[type = "Service"]', style: CyDomainStyle.service },
  { selector: 'node[type = "Provider"]', style: CyDomainStyle.provider },
  { selector: 'node[type = "Receiver"]', style: CyDomainStyle.receiver },
  { selector: 'node[type = "Parameter"]', style: CyDomainStyle.parameter },
  { selector: 'node[type = "Field"]', style: CyDomainStyle.field },
  { selector: 'node[type = "Literal"]', style: CyDomainStyle.string },
  { selector: ':selected', style: CyDomainStyle.active_node },
  { selector: 'edge', style: CyDomainStyle.global_edge }
];


/******************/
// Global variables

// Cytoscape
var cy;
var results = {"edges": [], "nodes": []};
var last_layout = CyLayouts[0];
// Neo4J
//var session;
var nodes = [], rels = [];
var stats = {}
// ACE Editor
var editor_query;
var langTools;
var editor_code;
var editor_props;
// Tabs
var tabs;
//var cy_panzoom;