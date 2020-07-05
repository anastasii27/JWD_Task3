$(document).ready(function ($) {
    let crewName;
    let departureAirport;
    let destinationAirport;
    let date;
    let time;

    //crews list
    $('.crews li').on('click', function () {
        $('.choose_crew_members ').hide();
        crewName = getCrewName($(this).text());
        showCrewAjax(crewName);
    });

    //crew edition
    $('.delete_crew_btn').on('click', function () {
        let resultConfirm = confirm("Are you really want to delete?");

        if(resultConfirm===true){
            let liToDelete =  $(this).parent();
            $.ajax({
                type: "POST",
                url: "/JWD_Task3_war/ajax",
                dataType:'json',
                data: {command: 'delete_crew',crew_name: crewName},

                success: function (data) {
                    if(data===true){
                        $(liToDelete).remove();
                        $('.crew_members ').hide();
                    }else {
                        //todo mes
                    }
                },
                error: function (data) {
                    $('.crew_members ').hide();
                    $('#crews_error').show();
                }
            });
        }
    });

    $(document).on('click','.delete_user_btn', function () {
        let crewMember = getCrewMemberName($(this).parent().text());
        let resultConfirm = confirm("Are you really want to delete?");

        if(resultConfirm===true){
            $.ajax({
                type: "POST",
                url: "/JWD_Task3_war/ajax",
                dataType:'json',
                data: {command: 'delete_crew_member',crew_name: crewName, user: crewMember},

                success: function (data) {
                    if(data===true){
                        showCrewAjax(crewName);
                    }
                },
                error: function (data) {
                    $('.crew_members ').hide();
                    $('#crews_error').show();
                }
            });
        }
    });

    $(document).on('click','#add_member_btn', function () {
        let crewMember = getCrewMemberName($(this).parent().text());

        $.ajax({
            type: "POST",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'add_crew_member',crew_name: crewName, user: crewMember},

            success: function (data) {
                if(data===true){
                    showCrewAjax(crewName);
                }
            },
            error: function (data) {
                $('.crew_members ').hide();
                $('#crews_error').show();
            }
        });
    });

    $('#add_user').on('click', function () {
        let pilots = $('#pilots_select').text();
        let stewards = $('#stewards_select').text();

        $.ajax({
            type: "GET",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'show_crew_members',crew_name: crewName},

            success: function (data) {
                $.each(data, function (user, userInfo) {
                    user = userInfo.name+ ' '+ userInfo.surname;
                    if(pilots.search(user)!==-1){
                        $("#pilots_select option:contains(\""+user+"\")").attr('disabled', 'disabled');
                    }
                    if(stewards.search(user)!==-1){
                        $("#stewards_select option:contains(\""+user+"\")").attr('disabled', 'disabled');;
                    }
                });
            },
            error: function (data) {
                $('.choose_crew_members ').hide();
                $('#crews_error').show();
            }
        });
    });

    $('#confirm_add').on('click', function () {
        let pilots = $('#pilots_select option:selected').text();
        let stewards = $('#stewards_select option:selected').text();

        $.ajax({
            type: "GET",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'add_crew_member',crew_name: crewName, user: pilots+stewards},

            success: function (data) {
                if(data===true){
                    showCrewAjax(crewName);
                }
            },
            error: function (data) {
                $('.choose_crew_members ').hide();
                $('#crews_error').show();
            }
        });
    });

    $('.form-group').on('change','#dep_country', function () {
        createAirportSelectAjax($(this).val(), '#dep_airport');
    });

    $('.form-group').on('change','#dest_country', function () {
        createAirportSelectAjax($(this).val(), '#dest_airport');
    });

    $('.form-group').on('change','#dep_airport', function () {
        departureAirport = $(this).val();
        $('#planes option').remove();

        freePlanesAjax(departureAirport, '#planes')
    });

    $('.form-group').on('change', '#edit_dep_airport', function () {
        departureAirport = $(this).val();
        $('#edit_planes option').remove();

        freePlanesAjax(departureAirport, '#edit_planes')
    });

    $('#dep_time, #dest_time').on('change', function () {
        time = $(this).val();

        if(departureAirport === 'Minsk(MSQ)'|| destinationAirport=== 'Minsk(MSQ)'){
            createFreeDispatchersSelectAjax(date, time, departureAirport);
        }
    });

    $('#dest_airport').on('change', function () {
        destinationAirport = $(this).val();

        if(destinationAirport === 'Minsk(MSQ)'){
            createFreeDispatchersSelectAjax(date, time, destinationAirport);
        }
    });

    $('#dep_flights_piker').datepicker({
        onSelect: function (){
            date = $('#dep_flights_piker').val();

            if(departureAirport === 'Minsk(MSQ)'|| destinationAirport=== 'Minsk(MSQ)'){
                createFreeDispatchersSelectAjax(date, time, departureAirport);
            }
        }
    });

    $('#dest_flights_piker').datepicker({
        onSelect: function (){
            date = $('#dest_flights_piker').val();

            if(departureAirport === 'Minsk(MSQ)'|| destinationAirport=== 'Minsk(MSQ)'){
                createFreeDispatchersSelectAjax(date, time, departureAirport);
            }
        }
    });

    $('#choose_crew_btn').on('click', function () {
        let crewName = $('.clicked td').eq(0).text();

        $.ajax({
            type: "GET",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'set_crew_for_flight',crew_name: crewName},

            success: function (data) {//todo modal
                if(data === true){
                    //
                }else {

                }
            },
            error: function (data) {
               alert('not created')
            }
        });
    });

    $(document).on('click', '.delete_flight_btn', function () {
        let flightNumber = $(this).parent().find('td').eq(0).text();
        let rowToDelete = $(this).parent();
        let departureDate = $('#btn').attr('dep_date');

        $.ajax({
            type: "POST",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'delete_flight', flight_number: flightNumber, departure_date:departureDate},

            success: function (data) {
                if(data === true){
                    $(rowToDelete).remove();
                }else {
                    alert("fuck")// todo message
                }
            },
            error: function (data) {
                alert('fuck') // todo message
            }
        });
    });

    $(document).on('click', '.edit_flight_btn',  function () {
        let flightNumber = $(this).parent().find('td').eq(0).text();
        let departureDate = $('#btn').attr('dep_date');

        $.ajax({
            type: "GET",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'flight_info', flight_number: flightNumber, departure_date:departureDate},

            success: function (data) {
                if(data.length!==0){
                    let plane = data.plane.model + '  ' + data.plane.number;
                    let departureCityWithAirport =  data.departureCity + '(' + data.departureAirportShortName + ')';
                    let departureDate = data.departureDate.year + '-' + addZeroBeforeValue(data.departureDate.month) + '-' + addZeroBeforeValue(data.departureDate.day);
                    let departureTime = addZeroBeforeValue(data.departureTime.hour) + ":" + addZeroBeforeValue(data.departureTime.minute);
                    let destinationCityWithAirport = data.destinationCity + '(' + data.destinationAirportShortName + ')';
                    let destinationDate = addZeroBeforeValue(data.destinationDate.year) + '-' + addZeroBeforeValue(data.destinationDate.month) + '-' + addZeroBeforeValue(data.destinationDate.day);
                    let destinationTime = addZeroBeforeValue(data.destinationTime.hour) + ":" + addZeroBeforeValue(data.destinationTime.minute)

                    $('#edit_flight_number').val(data.flightNumber);
                    $('#edit_planes').append('<option selected>' + plane + '</option>');
                    $('#edit_crew').append('<option selected>' + data.crewName + '</option>');
                    setFlightStatus(data.status);

                    $('#edit_dep_flights_piker').val(departureDate);
                    $('#edit_dep_time').val(departureTime);
                    $('#edit_dep_airport').append('<option selected>' +  departureCityWithAirport +'</option>');
                    $('#edit_dep_country').append('<p>' + data.departureCountry + '</p>');

                    $('#edit_dest_flights_piker').val(destinationDate);
                    $('#edit_dest_time').val(destinationTime);
                    $('#edit_dest_airport').append('<option selected>' +  destinationCityWithAirport + '</option>');
                    $('#edit_dest_country').append('<p>' + data.destinationCountry + '</p>');

                    addAirportsToSelectAjax(data.departureCountry, data.departureAirportShortName, '#edit_dep_airport');
                    addAirportsToSelectAjax(data.destinationCountry, data.destinationAirportShortName, '#edit_dest_airport');
                    addPlanesToSelectAjax(departureCityWithAirport, data.plane.number);
                    // addFreeCrewsToSelectAjax(data.departureDate.year + '-' + addZeroBeforeValue(data.departureDate.month) + '-' + addZeroBeforeValue(data.departureDate.day),
                    //     addZeroBeforeValue(data.departureTime.hour) + ":" + addZeroBeforeValue(data.departureTime.minute),
                    //     data.departureAirportShortName, data.crewName)
                }
            },
            error: function (data) {
                alert('fuck') // todo message
            }
        });
    })
});

function freePlanesAjax(departureAirport, selector) {
    let emptyOption = '<option selected>' + ' ' + '</option>';
    $.ajax({
        type: "GET",
        url: "/JWD_Task3_war/ajax",
        dataType:'json',
        data: {command: 'find_free_plane', departure_airport: departureAirport},

        success: function (data) {
            let option = '';

            $.each(data, function (airport, airportInfo) {
                option += '<option>' + airportInfo.model + ' '+ airportInfo.number + '</option>';
            });

            $(selector).append(emptyOption);
            $(selector).append(option);
        }
    });
}
function showCrewAjax(crewName) {

    $.ajax({
        type: "GET",
        url: "/JWD_Task3_war/ajax",
        dataType:'json',
        data: {command: 'show_crew_members',crew_name: crewName},

        success: function (data) {
            createMembersTable(data);
        },
        error: function (data) {
            $('.crew_members ').hide();
            $('#crews_error').show();
        }
    });
}

function createMembersTable(data) {
    let pilots = '';
    let stewardesses ='';
    let pilotCount = 0;
    let stewardCount = 0;

    $.each(data, function (user, userInfo) {
        if(userInfo.role === "pilot"){
            pilots += '<li class="list-group-item">'+ userInfo.name + ' ' + userInfo.surname + ' <button type="button" class="close delete_user_btn">&times;</li>';
            pilotCount++;
        }
        if(userInfo.role === "steward"){
            stewardesses += '<li class="list-group-item">'+ userInfo.name + ' ' + userInfo.surname + ' <button type="button" class="close delete_user_btn">&times;</li>';
            stewardCount++;
        }
    });

    // if(stewardCount===0 && pilotCount==0) {
    //     $('.crew_members').hide();
    //     $('#crews_error').show();
    // }else {
        $('.crew_members').show();
        $('#crews_error').hide();
        $('#pilots_list').empty().append(pilots);
        $('#steward_list').empty().append(stewardesses);
        if($('#edit_crew_btn').hasClass('clicked')){
            $('.close').show();
            $('#add_crew_btn').show();
        }else {
            $('.close').hide();
            $('#add_crew_btn').hide();
        }
    //}
}

function createAirportSelectAjax(country, airportSelect) {
    let emptyOption = '<option selected>' + ' ' + '</option>';
    $(airportSelect + ' option').remove();

    $.ajax({
        type: "GET",
        url: "/JWD_Task3_war/ajax",
        dataType:'json',
        data: {command: 'find_country_airport',country: country},

        success: function (data) {
            let option = '';

            $.each(data, function (airport, airportInfo) {
                option += '<option>' + airportInfo + '</option>';
            });

            $(airportSelect).append(emptyOption);
            $(airportSelect).append(option);
        },
        error: function (data) {
            $(airportSelect).append(emptyOption);
        }
    });
}

function createFreeDispatchersSelectAjax(date, time, airport) {
    let emptyOption = '<option selected>' + ' ' + '</option>';
    $('#dispatcher option').remove();

    console.log(date+time+airport);
    if(date !== null && time != null && airport != null){
        $.ajax({
            type: "GET",
            url: "/JWD_Task3_war/ajax",
            dataType:'json',
            data: {command: 'find_free_dispatcher',date: date, time:time, city_with_airport:airport},

            success: function (data) {
                let option = '';

                $.each(data, function (dispatcher, dispatcherInfo) {
                    option += '<option>' + dispatcherInfo.name + ' ' + dispatcherInfo.surname + '</option>';
                });

                $('#dispatcher').append(emptyOption);
                $('#dispatcher').append(option);
            },
            error: function (data) {
                $('#dispatcher').append(emptyOption);
            }
        });
    }
}

function addAirportsToSelectAjax(country, airportShortName, selector){
    $.ajax({
        type: "GET",
        url: "/JWD_Task3_war/ajax",
        dataType:'json',
        data: {command: 'find_country_airport',country: country},

        success: function (data) {
            let option = '';

            $.each(data, function (airport, airportInfo) {
                if(!airportInfo.includes(airportShortName)){
                    option += '<option>' + airportInfo + '</option>';
                }
            });

            $(selector).append(option);
        }
    });
}

function addPlanesToSelectAjax(cityWithAirport, planeNumber) {
    $.ajax({
        type: "GET",
        url: "/JWD_Task3_war/ajax",
        dataType:'json',
        data: {command :'find_free_plane', departure_airport:cityWithAirport},

        success: function (data) {
            let option = '';

            $.each(data, function (plane, planeInfo) {
                if(planeInfo.number !== planeNumber) {
                    option += '<option>' + planeInfo.model + ' ' + planeInfo.number + '</option>';
                }
            });

            $('#edit_planes').append(option);
        }
    });
}

function addFreeCrewsToSelectAjax(departureDate, departureTime, departureAirport, currentCrew) {
    $.ajax({
        type: "GET",
        url: "/JWD_Task3_war/ajax",
        dataType:'json',
        data: {command :'find_free_crew', departure_date:departureDate, departure_time:departureTime, departure_airport:departureAirport},

        success: function (data) {
            let option = '';

            $.each(data, function (crew, crewInfo) {
                if(crewInfo !== currentCrew) {
                    option += '<option>' + crewInfo + '</option>';
                }
            });

            $('#edit_crew').append(option);
        }
    });
}
