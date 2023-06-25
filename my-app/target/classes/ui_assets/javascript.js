$( document ).ready(function() {
    console.log( "ready!" );

    var button = $("#submit_button");   
    var searchBox = $("#search_text"); 
    var resultsTable = $("#results table tbody"); 
    var resultsWrapper = $("#results"); 

    button.on("click", function(){

        $.ajax({
          method : "POST",
          contentType: "application/json",
          data: createRequest(),
          url: "procesar_datos",
          dataType: "json",
          success: onHttpResponse
          });
      });

    function createRequest() {
        var searchQueryTmp = searchBox.val();

        var frontEndRequest = {
            searchQuery: searchQueryTmp,
        };
        
        return JSON.stringify(frontEndRequest);
    }

    function onHttpResponse(data, status) {
        if (status === "success" ) {
            console.log(data);
            addResults(data);
        } else {
            alert("Error al conectarse al servidor: " + status);
        }
    }

    function addResults(data) {
        resultsTable.empty();
        /*Variables de FrontendSearchResponse
        var cantidad = data.cantidad;
        var cadena = data.cadena;*/
        var frase = data.frase;
        /*Aquí se almacenan las 3 opciones de libros*/
        var libro1 = data.libro1;
        var libro2 = data.libro2;
        var libro3 = data.libro3;
        resultsWrapper.show();
        resultsTable.append("<thead><tr><th>Frase </th><th>Libro con mas coincidencias</th><th>Libro con coincidencias moderadas</th><th>Libro con menos coincidencias</th></tr></thead><tr><td>" + frase + "</td><td>" + libro1 + "</td><td>"+libro2+"</td><td>"+libro3+"</td></tr>");
    }
});