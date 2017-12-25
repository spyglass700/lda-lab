var processJsonFrom = function (url) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function () {
        var status = xhr.status;
        if (status === 200) {
            return xhr.response;
        } else {
            console.log('Something went wrong: ' + status);
        }
    };
    xhr.send();
};

var topicId = d3.select("#topicId").text();
var corpusName = d3.select("#corpusName").text();
var numberOfTopics = d3.select("#numberOfTopics").text();
var timestamp = d3.select("#timestamp").text();
var lda_url = "http://localhost:8080/corpus/" + corpusName + "/numberOfTopics/" + numberOfTopics + "/timestamp/" + timestamp;
var lda_api_url = "http://localhost:8080/api/corpus/" + corpusName + "/numberOfTopics/" + numberOfTopics + "/timestamp/" + timestamp;
var topic_url = lda_api_url + "/topic/" + topicId;

function showMoreTopicWords(tbody, data, max, n) {

    var currentLength = tbody.selectAll("tr").size();

    var rows = tbody.selectAll("tr")
        .data(data.wordProbabilities.slice(0, currentLength + n))
        .enter()
        .append("tr");

    rows.append("td")
        .append("a")
        .attr("href", function (row) {
            return lda_url + "/word/" + row['lemma'];
        })
        .text(function (row) {
            return row['lemma'];
        });

    rows.append("td")
        .text(function (row) {
            return row['probability'];
        });

    rows.append("td").classed("weight", true)
        .append("div")
        .classed("proportion", true)
        .style("margin-left", function (row) {
            return d3.format(".1%")(1 - row['probability'] / max);
        })
        .append("span")
        .classed("proportion", true)
        .text(function (row) {
            return row['probability'];
        });
}

function showLessTopicWords(tbody, data, n) {
    var currentLength = tbody.selectAll("tr").size();
    var rows = tbody.selectAll("tr")
        .data(data.wordProbabilities.slice(0, currentLength - n))
        .exit()
        .remove();
}

d3.request(topic_url)
    .mimeType("application/json")
    .response(function (xhr) {
        return JSON.parse(xhr.responseText);
    })
    .get(function (data) {
        var table = d3.select("#topicWordsTable");
        var tbody = table.append("tbody");

        var max = data.wordProbabilities[0]['probability'];

        showMoreTopicWords(tbody, data, max, 10);

        document.getElementById("topicWordsLess").onclick = function() {
            showLessTopicWords(tbody, data, 5);
        }
        document.getElementById("topicWordsMore").onclick = function() {
            showMoreTopicWords(tbody, data, max, 5);
        }
    });




// d3.request(topic_url)
//     .mimeType("application/json")
//     .response(function (xhr) {
//         return JSON.parse(xhr.responseText);
//     })
//     .get(function (json) {
//         var wordProbabilities = json.wordProbabilities;
//         var tableBody = d3.select("#test").append("tbody")
//         tableBody.text("wefweg");
//         // console.log(wordProbabilities);
//         var tableRows = tableBody.selectAll("tr").data(wordProbabilities).enter().append("tr");
//         tableRows.selectAll("tr").data(wordProbabilities).enter().append("td").text(function (d) {
//             console.log(d);
//             return JSON.stringify(d);
//         });
//     });

// var h3 = d3.selectAll("h3");
// var wordTable = d3.select("#test");
// var wordProbabilities = processJsonFrom(topic_url);
// console.log(wordProbabilities);
// h3.text("lfngweorgnw");
// wordTable.append("thead").append("th").text("wejiwbgewo");
// wordTableBody = wordTable.append("tbody");
// wordTableBody
//     .selectAll("tr")
//     .data(wordProbabilities)
//     .enter()
//     .append("td")
//     .text("wiuefbweougbw");

// .enter()
// .append("tr")
// .append("td")
// .append("a")
// .attr("href", function (d) {
//     return "http://localhost:8080/corpus/" + corpusName + "/numberOfTopics/" + numberOfTopics + "/timestamp/" + timestamp + "/word/" + d.word.lemma;
// })
// .text(function(d) { return d.word.lemma; })
// .append("td")
// .text(function(d) { return d.probability; });
