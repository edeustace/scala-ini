window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.LoadingPane

  ###
  Save parent node and register instance
  ###
  constructor: (@parentNodeSelector) ->

    @opts = 
      lines: 13
      length: 7
      width: 4
      radius: 10
      rotate: 0
      color: '#fff'
      speed: 1
      trail: 60
      shadow: false
      hwaccel: false
      className: 'spinner'
      zIndex: 2e9
      top: 'auto'
      left: 'auto' 

    null

  initParentNode: ->
    @parentNode = $(@parentNodeSelector)[0]

  show: ->
    @initParentNode() if !@parentNode?
    $(@parentNode).append "<div class='loading-pane'></div>"
    target = $('.loading-pane')[0];
    spinner = new Spinner(@opts).spin(target);
    null

  hide: ->
    $(".loading-pane").remove()
    null


window.instances = (window.instances || {})
window.instances.loadingPane = new com.ee.LoadingPane "body"
