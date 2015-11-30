<%@page import="org.json.simple.parser.JSONParser"%>
<%@page import="de.btu.openinfra.backend.OpenInfraProperties"%>
<%@page import="de.btu.openinfra.backend.db.daos.TopicInstanceDao"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="de.btu.openinfra.backend.helper.ImgSize"%>
<%@page import="de.btu.openinfra.backend.helper.ImgUrls"%>
<%@page import="de.btu.openinfra.backend.helper.ImgUrlHelper"%>
<%@page import="java.util.UUID"%>
<%@page import="de.btu.openinfra.backend.db.daos.AttributeTypeDao"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="../../snippets/Head.jsp" %>
	<title>OpenInfRA <fmt:message key="topicinstances.label"/></title>
</head>
<body>
	<%@ include file="../../snippets/Menu.jsp" %>
	
	
	
	<div id="orderAndFilterRow" class="row">
		<%@ include file="../../snippets/Filter.jsp" %>
		<%@ include file="../../snippets/OrderBy.jsp" %>
		<%@ include file="../../snippets/ResetFilterButton.jsp" %>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<fmt:message key="topicinstances.label"/>
			<c:set var="localizedStrings" value="${it[0].topicCharacteristic.descriptions.localizedStrings}"/>
			<%@ include file="../../snippets/LocalizedStrings.jsp" %>
			<span class="badge">
				<c:set var="currentCharacteristic" value="${it[0].topicCharacteristic.uuid}"/>
				<%=new TopicInstanceDao(
						UUID.fromString(ProjectDao.getCurrentProject(
								request.getAttribute("javax.servlet.forward.request_uri").toString())),
						OpenInfraSchemas.PROJECTS).getCount(
								UUID.fromString(pageContext.getAttribute("currentCharacteristic").toString()))%>
			</span>
			<span class="badge">
				<!-- link topic charateristic to map -->
				<!-- TODO: test if tc supports geometry attribute and is mapped to geoserver layer -->
		    	<a href="${contextPath}/rest/projects/maps?tc[0]=${currentCharacteristic}">
		    		<fmt:message key="maps.label"></fmt:message>
		        </a>
		    </span>
		</div>
		<c:set var="columnCount" value="${fn:length(columns)}"/>
		<c:if test="${columnCount == 0}">
			<div class="panel-body">
	    		<p><fmt:message key="nosettings.label"/></p>
	  		</div>
	  	</c:if>
		<table class="table">
			<thead>
				<tr>
					<c:forEach items="${columns}" var="column">
						<th style="width: ${100/columnCount}%">
						<%
							pageContext.setAttribute(
																"columnName",
																new AttributeTypeDao(
																UUID.fromString(ProjectDao.getCurrentProject(
																		request.getAttribute("javax.servlet.forward.request_uri").toString())),
																OpenInfraSchemas.PROJECTS).read(
																		PtLocaleDao.forLanguageTag(session.getAttribute("language").toString()), 
																		UUID.fromString(pageContext.getAttribute("column").toString())),
																PageContext.PAGE_SCOPE);
						%>
								<c:choose>
									<c:when test="${columnName.names.localizedStrings[0] != null}">
										${columnName.names.localizedStrings[0].characterString}
										<c:if test="${columnName.domain != null}">
											(<fmt:message key="domain.label"/>)
										</c:if>
									</c:when>
									<c:otherwise>
										-
									</c:otherwise>
								</c:choose>
							
						</th>
					</c:forEach>
					<c:if test="${columnCount == 0}">
						<th>
							Name
						</th>
						<th>
							UUID
						</th>
					</c:if>
				</tr>
			</thead>
			<c:forEach items="${it}" var="pojo">
				<tr>
					<c:forEach items="${columns}" var="column">
						<c:set var="found" value="false"/>
						<c:forEach items="${pojo.values}" var="value">
							<c:if test="${column == value.attributeTypeId}">
								<c:set var="found" value="true"/>
								<td>
									<c:if test="${value.attributeValueDomain != null}">
										<a href="../../topicinstances/${pojo.uuid}/topic?language=${language}&geomType=${geomType}">
											<c:forEach items="${value.attributeValueDomain.domain.names.localizedStrings}" var="item">
				  								<c:choose>
					  								<c:when test="${fn:endsWith(item.characterString, '.JPG') || fn:endsWith(item.characterString, '.jpg')}">
					  									<c:set var="currentImage" value="${item.characterString}"/>
							    						<img alt="<fmt:message key="noimage.label"/>: ${item.characterString}" 
							    									src="<%= ImgUrlHelper.getImgUrl(pageContext.getAttribute("currentImage").toString(), ImgSize.SMALL) %>"/>	
													</c:when>
													<c:otherwise>
														${item.characterString}
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</a>
									</c:if>
									<c:if test="${value.attributeValueValue != null}">
										<a href="../../topicinstances/${pojo.uuid}/topic?language=${language}&geomType=${geomType}">
											<c:forEach items="${value.attributeValueValue.value.localizedStrings}" var="item">
				  								<c:choose>
					  								<c:when test="${fn:endsWith(item.characterString, '.JPG') || fn:endsWith(item.characterString, '.jpg')}">
							    						<c:set var="currentImage" value="${item.characterString}"/>
							    						<img alt="<fmt:message key="noimage.label"/>: ${item.characterString}" 
							    									src="<%= ImgUrlHelper.getImgUrl(pageContext.getAttribute("currentImage").toString(), ImgSize.SMALL) %>"/>	
													</c:when>
													<c:otherwise>
														${item.characterString}
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</a>
									</c:if>
								</td> 
							</c:if>
						</c:forEach>
						<c:if test="${found == 'false'}">
							<td>
								-
							</td>
						</c:if>
					</c:forEach>
					<c:if test="${columnCount == 0}">
						<td>
							<a href="../../topicinstances/${pojo.uuid}/topic?language=${language}">
								<c:set var="localizedStrings" value="${pojo.topicCharacteristic.descriptions.localizedStrings}"/>
								<%@ include file="../../snippets/LocalizedStrings.jsp" %>
							</a>
						</td>
						<td>
							<a href="../../topicinstances/${pojo.uuid}/topic?language=${language}">
								${pojo.uuid}
							</a>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</div>
	
	<nav>
	  <ul class="pager">
	    <c:set var="navParameter" value=""/>
		<c:forEach items="${param}" var="pageParameter">
			<c:if test="${pageParameter.key != 'offset' && pageParameter.key != 'size'}">
				<c:set var="ppValue" value="${pageParameter.value}"/>
				<c:set var="navParameter">${navParameter}&${pageParameter.key}=<%=URLEncoder.encode(pageContext.getAttribute("ppValue").toString(), "UTF-8")%> </c:set>
			</c:if>
		</c:forEach>
		<c:set var="size" value="${param.size}"/>
		<c:set var="offset" value="${param.offset}"/>
		<c:if test="${param.size == null}">
			<c:set var="size">
				<%=OpenInfraProperties.DEFAULT_SIZE%>
			</c:set>
			<c:set var="offset">
				<%=OpenInfraProperties.DEFAULT_OFFSET%>
			</c:set>
		</c:if>
  		<c:if test="${fn:length(it) == size}">
  			<c:set var="offset" value="${offset + size}"/>
  			<li class="next">
  				<a href="${forward}?${navParameter}&offset=${offset}&size=${size}">
  					<fmt:message key="next.label"/> 
  					<span aria-hidden="true">&rarr;</span>
  				</a>
  			</li>
  		</c:if>
  		<c:if test="${(offset > 0) && offset - size != 0}">
  			<c:set var="offset" value="${offset - (size+size)}"/>
  			<li class="previous">
  				<a href="${forward}?${navParameter}&offset=${offset}&size=${size}">
  					<span aria-hidden="true">&larr;</span> 
  					<fmt:message key="previous.label"/>
  				</a>
  			</li>
  		</c:if>
	    
	  </ul>
	</nav>
	
</body>
</html>