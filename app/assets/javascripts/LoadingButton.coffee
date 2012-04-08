window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.LoadingButton 
  constructor: (@id, @clickHandler) ->
    $(@id).click (e) => 
      return if $(@id).hasClass("disabled")
      @clickHandler e
  
  loading: (isLoading) ->
    if isLoading
      $(@id)
        .addClass("disabled")
        .find("i")
        .removeClass("icon-play")
        .addClass("icon-cog")

      window.instances.loadingPane.show() if window.instances.loadingPane?
    else
      $(@id)
        .removeClass("disabled")
        .find("i")
        .removeClass("icon-cog")
        .addClass("icon-play")


      window.instances.loadingPane.hide() if window.instances.loadingPane?
    null      
