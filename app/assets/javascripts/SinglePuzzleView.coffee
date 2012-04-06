window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.SinglePuzzleView


  ###
  @param solveUrl - the url to post solutions to
  @param editor - the ACE editor
  ###
  constructor: (@solveUrl, @editor)->
    @processor = new com.ee.string.StringUpdateProcessor()
    @hook = new com.ee.string.AceEditorHook @editor, @processor
    @runButton = new com.ee.LoadingButton "#runButton", (e) => @runCode() 
    @evalHighlighter = new com.ee.EvaluationHighlighter @editor
    @bindListenersToEditor()
    @infoBox = new com.ee.InfoBox()
    null

  bindListenersToEditor: ->
    @editor.getSession().on 'change', (e) => @infoBox.hide()
    @editor.getSession().selection.on 'changeSelection', (e) => @infoBox.hide() 
    null

  setPuzzle: (@puzzle) -> 
    @evalHighlighter.clear()
    @processor.init @puzzle

  addSuccessCallback: (@successCallback) ->
  addFailedCallback: (@failedCallback) ->


  ###
  Run the typed code against the server
  ###
  runCode: ->
    @runButton.loading true

    params = 
      id: @puzzleId, 
      solution: @editor.getSession().getValue()
    
    $.post @solveUrl, 
        params,
        (data, textStatus,jqXHR) => @handleRunResponse data
    null

  handleRunResponse: (data) ->
    @runButton.loading false
    
    @evalHighlighter.clear()

    if data.successful == true 
      @successCallback() if @successCallback?
    else
      @infoBox.error data.result.summary 
      @failedCallback() if @failedCallback?

    if data.result.evaluations?
      @evalHighlighter.show data.result.evaluations
    null

