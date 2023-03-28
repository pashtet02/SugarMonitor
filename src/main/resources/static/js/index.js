Highcharts.chart('container', {
    chart: {
        type: 'line',
        width: 500
    },
    title: {
        text: 'Line chart'
    },
    xAxix: {
        categories: ["14", "15", "16", "17", "18", "19"]
    },
    tooltip: {
        formatter: function () {
            console.log(this)
        }
    },
    series: [{
        data: [2, 3, 4, 6, 10, 14, 22]
    }]
    });