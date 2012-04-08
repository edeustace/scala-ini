window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.CreatePuzzleView

  ###
  # @param solveUrl - the url to post solutions to
  # @param editor - the ACE editor
  # @param defaultEditorText - the default text in the editor what we can delete/restore
  # should the user choose that link.
  ###
  constructor: (@solveUrl, @saveUrl, @editor, @defaultEditorText, @puzzleStart = "/*<<*/", @puzzleEnd = "/*>>*/" )->
    #console.log "CreatePuzzleView constructor solveUrl: #{@solveUrl}"
    @testButton = new com.ee.LoadingButton("#testButton", (e) => @onTestButtonClick e)
    @saveButton = new com.ee.LoadingButton("#saveButton", (e) => @onSaveButtonClick e)
    @evalHighlighter = new com.ee.EvaluationHighlighter(@editor)
    @bindListenersToEditor()
    null

  bindListenersToEditor: ->
    @editor.getSession().on 'change', (e) => @onEditorChange e
    @editor.getSession().selection.on 'changeSelection', (e) => @onEditorChange e
    @editor.getSession().selection.on 'changeCursor', (e) => @onEditorCursorChange e
    null

  onEditorChange: (e) ->
    @removeErrorBox()
    @removeSuccessBox()
    null

  removeErrorBox: -> 
    @_removeBox("#errorBox")

  onEditorCursorChange: (e) ->
    if @editor.getSession().getValue() == @defaultEditorText && !@isResetting
      @editor.getSession().setValue ''
    null

  removeSuccessBox: ->
    @_removeBox("#successBox") 

  _removeBox: (id)->  
    $(id).addClass 'invisible' 
  onTestButtonClick: (e) ->
    #console.log "onTestButtonClick"
    @testButton.loading true
    @runCode()
    null

  onSaveButtonClick: (e) ->
    #console.log "onSaveButtonClick"
    @saveButton.loading true
    @saveCode()
    null


  ###
  # Run the typed code against the server
  ###
  runCode: ->

    params = 
      solution: @editor.getSession().getValue()

    @_invoke @solveUrl, params, @onTestResponse
    null


  onTestResponse: (data) ->
    @testButton.loading false

    #console.log data
    
    if data.successful == true 
      #console.log "success"
      @evalHighlighter.clear()
    else 
      #console.log "failure: #{data.result.summary}"
      $("#errorBox")
        .html("Error: #{data.result.summary}")
        .removeClass('invisible')

    if data.result.evaluations?
      @evalHighlighter.show(data.result.evaluations) 
    null

  ###
  # Save the code on the server
  ###   
  saveCode: ->
      params = 
        solution: @editor.getSession().getValue()
      @_invoke @saveUrl, params, @onSaveResponse
    null

  _invoke: (url, params, callback) ->
    $.post( url, 
        params,
        (data, textStatus,jqXHR) => callback.call this, data
      )
    null

  onSaveResponse: (data) ->
    @saveButton.loading false
    if data.successful == true
      #console.log "urlKey: #{data.urlKey}"
      document.location.href = "/puzzles/key/#{data.urlKey}"
    else
      $("#errorBox")
        .html(data.message)
        .removeClass('invisible')
    null
 
  handleRunResponse: (data) ->
    #console.log(data)
    if data.successful == true 
      #console.log "success"
      $("#successBox")
        .html("Success!")
        .removeClass('invisible')
    else 
      #console.log "failure: #{data.exception}"
      $("#errorBox")
        .html("Error: #{data.message}")
        .removeClass('invisible')
    null
