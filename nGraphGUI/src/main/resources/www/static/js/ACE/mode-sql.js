ace.define("ace/mode/sql_highlight_rules", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text_highlight_rules"], function(e, t, n) {
    "use strict";
    var r = e("../lib/oop"),
        i = e("./text_highlight_rules").TextHighlightRules,
        s = function() {
		var e = "match|optional|start|return|with|unwind|where|order|by|skip|limit|using|index|seek|scan|join|create|delete|detach|set|remove|foreach|merge|on|call|yield|unique|union|all|load|csv|periodic|commit|drop|constraint",
    	t = "true|false|null",
    	n = "all|any|exists|none|single|coalesce|endnode|head|id|last|length|properties|randomuuid|size|size|startnode|timestamp|toboolean|tofloat|tointeger|type|avg|collect|count|max|min|percentilecont|percentiledisc|stdev|stdevp|sum|extract|filter|keys|labels|nodes|range|reduce|relationships|reverse|tail|abs|ceil|floor|rand|round|sign|e|exp|log|log10|sqrt|acos|asin|atan|atan2|cos|cot|degrees|haversin|pi|radians|sin|tan|left|ltrim|replace|reverse|right|rtrim|split|substring|tolower|tostring|toupper|trim|date|date|transaction|date|statement|date|realtime|date|truncate|datetime|transaction|statement|realtime|truncate|localdatetime|transaction|statement|realtime|localtime|time|duration|between|inmonths|indays|inseconds|distance|point",
    	r = "int|numeric|decimal|date|varchar|char|bigint|float|double|bit|binary|text|set|timestamp|money|real|number|integer",
                i = this.createKeywordMapper({
                    "support.function": n,
                    keyword: e,
                    "constant.language": t,
                    "storage.type": r
                }, "identifier", !0);
            this.$rules = {
                start: [{
                    token: "comment",
                    regex: "--.*$"
                }, {
                    token: "comment",
                    start: "/\\*",
                    end: "\\*/"
                }, {
                    token: "string",
                    regex: '".*?"'
                }, {
                    token: "string",
                    regex: "'.*?'"
                }, {
                    token: "string",
                    regex: "`.*?`"
                }, {
                    token: "constant.numeric",
                    regex: "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"
                }, {
                    token: i,
                    regex: "[a-zA-Z_$][a-zA-Z0-9_$]*\\b"
                }, {
                    token: "keyword.operator",
                    regex: "\\+|\\-|\\/|\\/\\/|%|<@>|@>|<@|&|\\^|~|<|>|<=|=>|==|!=|<>|="
                }, {
                    token: "paren.lparen",
                    regex: "[\\(]"
                }, {
                    token: "paren.rparen",
                    regex: "[\\)]"
                }, {
                    token: "text",
                    regex: "\\s+"
                }]
            }, this.normalizeRules()
        };
    r.inherits(s, i), t.SqlHighlightRules = s
}), ace.define("ace/mode/sql", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/sql_highlight_rules"], function(e, t, n) {
    "use strict";
    var r = e("../lib/oop"),
        i = e("./text").Mode,
        s = e("./sql_highlight_rules").SqlHighlightRules,
        o = function() {
            this.HighlightRules = s, this.$behaviour = this.$defaultBehaviour
        };
    r.inherits(o, i),
        function() {
            this.lineCommentStart = "--", this.$id = "ace/mode/sql"
        }.call(o.prototype), t.Mode = o
});
(function() {
    ace.require(["ace/mode/sql"], function(m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();