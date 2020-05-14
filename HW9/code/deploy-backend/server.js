const express = require('express');
const path = require('path');
const app = express();
const axios = require('axios').default;
const googleTrends = require('google-trends-api');

app.use(express.static(path.join(__dirname, 'build')));

const GUARDIAN_KEY = '9cbebbba-c408-4c44-8c20-b0f9b3d04fce';

app.get('/', function (req, res) {
    res.send("Hello World!");
});

const cors = require('cors');
app.use(cors());

/* *********************************************** */
// Guardian URL for Home tab: /guardian
// Guardian URL for section tabs: /guardian?section=
app.get('/guardian', function (req, res) {
    // https://content.guardianapis.com/search?api-key=9cbebbba-c408-4c44-8c20-b0f9b3d04fce&section=(sport|business|technology|politics)&show-blocks=all
    let url = '';
    if (req.query.section == null) {
        url = 'https://content.guardianapis.com/search?api-key=' + GUARDIAN_KEY
            + '&order-by=newest&show-fields=starRating,headline,thumbnail,short-url';
    } else {
        url = 'https://content.guardianapis.com/' + req.query.section + '?api-key=' + GUARDIAN_KEY + '&show-blocks=all';
    }

    axios.get(url)
        .then(function (response) {
            let results = response.data.response.results;
            let returnList = [];
            for (let i = 0; i < results.length; i++) {
                let result = results[i];
                let obj = {};
                obj['id'] = result.id;
                obj['articleTitle'] = result.webTitle;
                obj['articleSection'] = result.sectionName;
                obj['articleDate'] = result.webPublicationDate;
                obj['shareUrl'] = result.webUrl;

                let image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                if (req.query.section == null) { // guardian
                    if (result.fields != null && result.fields.thumbnail != null && result.fields.thumbnail !== '') {
                        image = result.fields.thumbnail;
                    }
                } else { // section
                    let blocks = result.blocks;
                    if (blocks.main != null && blocks.main.elements != null && blocks.main.elements[0].assets != null) {
                        let assets = result.blocks.main.elements[0].assets;
                        if (assets[assets.length - 1] != null && assets[assets.length - 1].file != null && assets[assets.length - 1].file !== '') {
                            image = assets[assets.length - 1].file;
                        }
                    }
                }
                obj['articleImage'] = image;

                returnList.push(obj);
            }
            // res.send({results: response.data.response.results});
            res.send({results: returnList});
        })
        .catch(function (error) {
            console.error(error);
        });
});

/* *********************************************** */
// For article detail: /article?id=
app.get('/article', function (req, res) {
    let image;
    let returnObj = {};
    let url = 'https://content.guardianapis.com/' + req.query.id + '?api-key=' + GUARDIAN_KEY + '&show-blocks=all';
    axios.get(url)
        .then(function (response) {
            let data = response.data.response.content;
            if (data.blocks.main == null ||
                data.blocks.main.elements == null ||
                data.blocks.main.elements[0].assets == null) {
                image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
            } else {
                let assets = data.blocks.main.elements[0].assets;
                if (assets[assets.length - 1] == null ||
                    assets[assets.length - 1].file == null ||
                    assets[assets.length - 1].file === '') {
                    image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                } else {
                    image = assets[assets.length - 1].file;
                }
            }
            returnObj['image'] = image;

            let description = '';
            let bodies = data.blocks.body;
            bodies.forEach(item => {
                description += item.bodyHtml;
            });
            returnObj['description'] = description;

            returnObj['title'] = data.webTitle;
            returnObj['date'] = data.webPublicationDate;
            returnObj['section'] = data.sectionName;
            returnObj['shareUrl'] = data.webUrl;

            res.send(returnObj);
        })
        .catch(function (error) {
            console.error(error);
        });
});

/* *********************************************** */
// For article search: /search?q=
app.get('/search', function (req, res) {
    let resultObj = {};
    let url = 'https://content.guardianapis.com/search?q=' + req.query.q + '&api-key=' + GUARDIAN_KEY + '&show-blocks=all';
    axios.get(url)
        .then(function (response) {
            const responseOne = response.data.response.results.slice(0, 10);
            let resultOne = [];
            responseOne.forEach(item => {
                let obj = {};
                if (item.blocks.main == null ||
                    item.blocks.main.elements == null ||
                    item.blocks.main.elements[0].assets == null) {
                    obj["image"] = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                } else {
                    let assets = item.blocks.main.elements[0].assets;
                    if (assets[assets.length - 1] == null ||
                        assets[assets.length - 1].file == null ||
                        assets[assets.length - 1].file === '') {
                        obj["image"] = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                    } else {
                        obj["image"] = assets[assets.length - 1].file;
                    }
                }
                obj["id"] = item.id;
                obj["title"] = item.webTitle;
                obj["date"] = item.webPublicationDate;
                obj["section"] = item.sectionName;
                obj["shareUrl"] = item.webUrl;
                resultOne.push(obj);
            });
            resultObj = resultOne;
            res.send({results: resultObj});
        })
        .catch(errors => {
            console.error(errors);
        });
});

/* *********************************************** */
// For Google Trends API: /trend?q=
app.get('/trend', function (req, res) {
    googleTrends.interestOverTime({keyword: req.query.q, startTime: new Date('2019-06-01')})
        .then(function(results){
            let data = JSON.parse(results).default.timelineData;
            let returnList = [];
            data.forEach(item => {
                returnList.push(item.value[0]);
            });
            res.send({results: returnList});
        })
        .catch(function(err){
            console.error(err);
        });
});

app.listen(process.env.PORT || 8080);