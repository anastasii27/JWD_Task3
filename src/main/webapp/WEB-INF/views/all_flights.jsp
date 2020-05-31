<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri= "http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored ="false" %>
<html>
    <head>
        <title>Flights</title>

        <fmt:setLocale value="${sessionScope.local}" />
        <fmt:setBundle basename="localization.local" var="loc" />
        <fmt:message bundle="${loc}" key="local.label.plane" var="plane_label" />
        <fmt:message bundle="${loc}" key="local.label.dep_time" var="dep_time_label" />
        <fmt:message bundle="${loc}" key="local.label.dest_time" var="dest_time_label" />
        <fmt:message bundle="${loc}" key="local.label.dest_city" var="dest_city_label" />
        <fmt:message bundle="${loc}" key="local.label.dep_city" var="dep_city_label" />
        <fmt:message bundle="${loc}" key="local.label.flight" var="flight_label" />
        <fmt:message bundle="${loc}" key="local.label.status" var="status_label" />

        <link rel="stylesheet" href="${pageContext.request.contextPath}/design/css/header.css"/>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
        <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

        <script>
            $(document).ready(function () {
                $('.lang').on('click', function (){
                    $('.lang').append('<input type="hidden" name="url" value="${pageContext.request.contextPath}/mmm?action=show_flights_page"/>');
                });
            });
        </script>

    </head>
    <body>
        <jsp:include page="header.jsp"/>

        <c:if test="${requestScope.result ne null}">
            <c:out value="${requestScope.result}"/>
        </c:if>

        <c:if test="${requestScope.result eq null}">
            <table class ="table" border="2">
                <tr>
                    <th>${flight_label}</th><th>${dep_time_label}</th> <th>${dep_city_label}</th>
                    <th>${dest_time_label}</th><th>${dest_city_label}</th><th>${plane_label}</th><th>${status_label}</th>
                </tr>
                <c:forEach items="${requestScope.flight}" var="flight_item">
                    <tr onclick="document.location.href= '${pageContext.request.contextPath}/mmm?action=show_flight_info&group=${flight_item.groupName}&flight_number=${flight_item.flightNumber}&departure_date=${flight_item.departureDate}'">
                        <td>${flight_item.flightNumber}</td>
                        <td>${flight_item.departureTime}</td>
                        <td>${flight_item.departureCity}(${flight_item.departureAirportShortName})</td>
                        <td>${flight_item.destinationTime}</td>
                        <td>${flight_item.destinationCity}(${flight_item.destinationAirportShortName})</td>
                        <td>${flight_item.planeModel}</td>
                        <td>${flight_item.status}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </body>
</html>