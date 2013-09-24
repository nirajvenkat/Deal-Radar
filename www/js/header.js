// DEVELOPER NOTE: header links should be added to the array below
  var headerItems = [
];

var BRAND_TITLE = "Deal Radar";

function header() {
  var s = '<div class="navbar-inner"><div class="container-fluid"><button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">';
  s += '<span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></button><a class="brand" href="index.html">'+BRAND_TITLE+'</a><div class="nav-collapse collapse">';
  s += '<ul class="nav">';

  for(var i=0; i<headerItems.length; i++) {
    s += renderHeaderItem(headerItems[i]);
  }
    
  s += '</ul></div><!--/.nav-collapse --></div></div>'

  $("#header").html(s);
  $("#header").before("<div class=\"gradient\" id=\"headerGradient\"></div>");
}

function renderHeaderItem(itm) {
  s = '<li><a href="' + itm.target + '"' + (itm.newTab ? ' target="_blank"' : '') + '>' + itm.text + '</a></li>';
  return s;
}

function showOrHideGradient() {
    if (matchMedia('only screen and (max-width: 768px)').matches || matchMedia('only screen and (max-width: 979px)').matches) {
      // Mobile screen mode      
      $("#headerGradient").remove();
    } else {
      if ($("#headerGradient").length == 0)
        $("#header").before("<div class=\"gradient\" id=\"headerGradient\"></div>");
    }
}

window.onresize=function(){
  showOrHideGradient();
};

$(document).ready(
  function() {
    header();
    showOrHideGradient();
  }
);