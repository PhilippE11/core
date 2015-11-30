<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="../../snippets/Head.jsp" %>
    <title> OpenInfRA <fmt:message key="search.results.label"/></title>
</head>
<body>
 	<%@ include file="../../snippets/Menu.jsp" %>
 	
 	<fmt:message key="search.statistics">
 		<fmt:param><span id="resultCount"><b></b></span></fmt:param>
 		<fmt:param><span id="elapsedTime"><b></b></span></fmt:param>
 	</fmt:message>
 	<br /><br />
 	
 	<div class="panel-group">
		<table class="table">
			<thead>
				<tr>
					<th>
						<fmt:message key="topicinstances.label"/>
					</th>
					<th>
						<fmt:message key="topiccharacteristics.label"/>
					</th>
					<th>
						<fmt:message key="projects.label"/>
					</th>
					<th>
						<fmt:message key="searchresult.label"/>
					</th>
				</tr>
			</thead>
			<tbody id="results">
				
			</tbody>
		</table>
	</div>
	
	<div class="urlImageContainer">
		<img id="url" align="middle" src="${contextPath}/img/url.gif"/>
	</div>
	
	<nav>
  		<ul class="pager">
  		</ul>
	</nav>
    
	<script language="javascript" type="text/javascript">

		$(document).ready(function() {
		    var start = getUrlParameter("start");
			var rows = getUrlParameter("rows");
			var data = new Object();
			var dataParts = [];
			var dataPart = new Object();
			
			// this is just a workaround for the GUI
			if (start == null || start < 0) { start = 0; }
			if (rows == null || rows < 0) { rows = 20; }
			
			// determine from which document we come from and build the data 
			// object with the information from the input field
		    if (document.referrer.indexOf("search/extended") > 0 ||
		            (document.referrer.indexOf("search/result") > 0 &&
		                    getUrlParameter("query") == null)) {
		        // iterate over value of the counter parameter
		        for(var i = 1; i <= getUrlParameter("hc"); i++) {
		            dataPart = new Object();
			        dataPart["mandatory"] = getUrlParameter("m"+i);
			        dataPart["attributeType"] = getUrlParameter("at"+i);
			        dataPart["relationalOperator"] = getUrlParameter("ro"+i);
			        dataPart["attributeValue"] = getUrlParameter("av"+i);
			        dataPart["tillAttributeValue"] = getUrlParameter("tav"+i);
			        dataPart["logicOperator"] = getUrlParameter("lo"+i);
			        dataPart["relevance"] = getUrlParameter("r"+i);
			        if (getUrlParameter("f"+i) == null) {
			            dataPart["fuzziness"] = false;
			        } else {
			            dataPart["fuzziness"] = true;
			        }
			        
			        dataParts.push(dataPart);
		        }

		        // add all parts to the data object
		        data["complexQueryPart"] = dataParts;
		    } else {
			    data["rawSolrQuery"] = getUrlParameter("query");
		    }
			
		    if (data["rawSolrQuery"] != "" || data["complexQueryPart"] != null && 
		            data["complexQueryPart"][0]["attributeValue"] != "") {
		        // get all parameters
		        var param = parseEncoding(window.location.search.substring(1));
		        
		     	// append the parameters and replace the start and row parameter
				param = param.replace(new RegExp("start=\\d+"), start);
				param = param.replace(new RegExp("rows=\\d+"), rows);
		     
		     	// create the base path of the application
				var searchPath = "${contextPath}/rest/v1/search?" + param;
				
				// set the new data
				$.ajax({
					url: searchPath,
					type: "POST",
					data: JSON.stringify(data),
					contentType: "application/json; charset=utf-8",
					cache: false,
			 		error: function(xhr){
			 		   $(".urlImageContainer").remove();
				  	}, // end error
					success: function(result) {
					    // write the results
					    for (var key in result["databaseResult"]) {
					        addLine(result["databaseResult"][key]);
					    }
						// write the result count
						$("#resultCount b").text(result["resultCount"]);
						createPagination(result["resultCount"], start, rows);
					    
					    // write the elapsed time
					    $("#elapsedTime b").text(result["elapsedTime"]/1000);
					    
					    // hide the loading image
					    $(".urlImageContainer").remove();
					} // end success
			  	}); // end ajax call
			} else {
			    // write the result count
				$("#resultCount b").text("0");
			    
			    // write the elapsed time
			    $("#elapsedTime b").text("-1");
			    // hide the loading image
			    $(".urlImageContainer").remove();
			    
			}
		});
		
		function addLine(result) {
		    var tiPath = "${contextPath}/rest/v1/projects/";
		    var tmpLabel = "inWork";
		    
		    // create the tr element and add a working label id
		    $("#results").append("<tr id=\"" + tmpLabel + "\"></tr>");
		    
		    for (var key in result) {
		        if (key != "uuid" && key != "trid") {
	        		if (key == "highlight") {
	        		    var text = "";
	        		    for (var hl in result[key]) {
	        		    	text += hl +":"+ result[key][hl] + " ... ";
	        		    }
	        		    
	        		    $("#"+ tmpLabel).append("<td>"+ text.substring(0, text.length - 4) +"</td>");
	        		    
			        } else {
			            var res = result[key];
			            if(key =="topicInstanceId") {
			                res = "<a href=\""+ tiPath + result["projectId"] + "/topicinstances/" + result[key] + "/topic\" >"+ res +"</a>";
			            }
			            $("#"+tmpLabel).append("<td>"+ res +"</td>");
			        }
		        }
		    }
		    
		    // remove the working label
		    $("#"+ tmpLabel).removeAttr("id");
		    
		};
		
		function parseEncoding(input) {
		    try {
		        // If the string is UTF-8, this will work and not throw an error.
		        return decodeURIComponent(window.location.search.substring(1));
		    } catch(e) {
		        // If it isn't, an error will be thrown, and we can asume that we have an ISO string.
		        return unescape(window.location.search.substring(1));
		    }
		}
		
		function getUrlParameter(sParam) {
		    
		    var sPageURL = parseEncoding(window.location.search.substring(1)),
		        sURLVariables = sPageURL.split('&'),
		        sParameterName,
		        i;
				
		    for (i = 0; i < sURLVariables.length; i++) {
		        sParameterName = sURLVariables[i].split('=');

		        if (sParameterName[0] === sParam) {
		            return sParameterName[1] === undefined ? true : sParameterName[1];
		        }
		    }
		};
		
		function createPagination(count, start, rows) {
		    var resultsPerPage = 20;
		    var pages = count % resultsPerPage;
		    var url = window.location.href;
			var disableA = "style=\"pointer-events:none;";
			var prevDisabled = "";
			var nextDisabled = "";
			
		    var prev = 0;
			var next = 0;
			
		    next = parseInt(start) + parseInt(rows);
		    prev = parseInt(start) - parseInt(rows);
		    
		    
		    if (prev < 0) {
				prevDisabled = disableA;
		    }
		    
		    if (next >= count) {
				nextDisabled = disableA;
		    }
		    
		    $(".pager").append("<li><a "+ prevDisabled +" href=\""+ url.replace(/start=\d+/g, "start="+ prev) +"\" >Previous</a></li>");
		    $(".pager").append("<li><a "+ nextDisabled +" href=\""+ url.replace(/start=\d+/g, "start="+ next) +"\" >Next</a></li>");
		}
	</script>
	
</body>
</html>