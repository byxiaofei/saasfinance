var yearJson = [
    {"value":"2015", "text":"2015"},
    {"value":"2016", "text":"2016"},
    {"value":"2017", "text":"2017"},
    {"value":"2018", "text":"2018"},
    {"value":"2019", "text":"2019"},
    {"value":"2020", "text":"2020"},
    {"value":"2021", "text":"2021"},
    {"value":"2022", "text":"2022"},
    {"value":"2023", "text":"2023"},
    {"value":"2024", "text":"2024"},
    {"value":"2025", "text":"2025"},
    {"value":"2026", "text":"2026"},
    {"value":"2027", "text":"2027"},
    {"value":"2028", "text":"2028"},
    {"value":"2029", "text":"2029"},
    {"value":"2030", "text":"2030"},
    {"value":"2031", "text":"2031"},
    {"value":"2032", "text":"2032"},
    {"value":"2033", "text":"2033"},
    {"value":"2034", "text":"2034"},
    {"value":"2035", "text":"2035"}
]

function getCurrentYear() {
    return (new Date()).getFullYear()+'';
}

function getCurrentMonth() {
    var month = (new Date()).getMonth()+1;
    return month<10?('0'+month):month+'';
}

function getCurrentDay() {
    return (new Date()).getDate()+'';
}

function getCurrentData(){
    var data = new Date();
    var y = data.getFullYear();
    var m = data.getMonth()+1;
    var d = data.getDate();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}