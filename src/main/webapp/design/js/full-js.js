$(document).ready(function ($) {

    $('.datepicker-here').datepicker({
        dateFormat: 'yyyy-mm-dd',
        firstDay: 1,
        minDate: getMinDate(10),
        maxDate: getMaxDate(31)
    });

    //menu
    $(function () {
        let url = window.location.href;
        let action = getActionFromURL(url);

        $('#menu li').each(function () {
            let menu_url = $(this).find('a').attr('href');
            let menu_action = getActionFromURL(menu_url);

            if (action ==menu_action) {
                $(this).addClass('active');
            }
        });
    });

    //arrivals departures
    $('.arr_dep').hide();
    $('#no_flights').hide();
    $('.crew_members').hide();
    $('#crews_error').hide();

    $(document).on('click', '.crews li', function () {
        $('.crews li').css('border', '');
        $(this).css('border', '1px solid #0f1970');
    });

});

function getMaxDate(days) {
    let date = new Date();

    date.setDate(date.getDate() + days);
    return date;
}

function getMinDate(days) {
    let date = new Date();

    date.setDate(date.getDate() - days);
    return date;
}

function getActionFromURL(url) {

    let start = url.indexOf("=");
    let end = url.indexOf("&");

    if(end===-1){
        return url.substring(start);
    }
    return url.substring(start, end);
}

function determineTableType(flightType, tableLine) {

    if(flightType === 'departure'){
        $('#dep_table').show();
        $('#departures').append(tableLine);
    }
    if(flightType === 'arrival'){
        $('#arr_table').show();
        $('#arrivals').append(tableLine);
    }
}
