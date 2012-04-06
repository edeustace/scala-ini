window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.InfoBox
  error: (message) ->
    $("#errorBox")
      .html("Error: #{message}")
      .removeClass('invisible')
    null

  hide: ->
    $("#errorBox")
      .addClass("invisible")
    null
