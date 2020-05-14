const express = require('express');
const path = require('path');
const app = express();
const axios = require('axios').default;

app.use(express.static(path.join(__dirname, 'build')));

const GUARDIAN_KEY = '9cbebbba-c408-4c44-8c20-b0f9b3d04fce';
const NYTIMES_KEY = 'qUgqrlxToGTQge5poYhwfbugpIuXPuG3';

app.get('/', function (req, res) {
    res.sendFile("Hello!");
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
            + '&section=(sport|business|technology|politics)&show-blocks=all';
    } else {
        url = 'https://content.guardianapis.com/' + req.query.section + '?api-key=' + GUARDIAN_KEY + '&show-blocks=all';
    }
    axios.get(url)
        .then(function (response) {
            let results = response.data.response.results;
            let returnList = [];
            let cnt = 0;
            for (let i = 0; i < results.length && cnt < 10; i++) {
                let result = results[i];
                let obj = {};
                let id = result.id;
                let title = result.webTitle;
                let section = result.sectionId;
                let date = result.webPublicationDate.slice(0, 10);
                let blocks = result.blocks;
                let shareUrl = result.webUrl;
                if (title == null || title === '' || section == null || section === '' ||
                    date == null || date === '' || blocks == null || blocks === '' ||
                    shareUrl == null || shareUrl === '' || id == null || id === '') {
                    continue;
                }
                if (blocks.body == null || blocks.body[0] == null ||
                    blocks.body[0].bodyTextSummary == null || blocks.body[0].bodyTextSummary === '') {
                    continue;
                }
                obj['id'] = id;
                obj['articleTitle'] = title;
                obj['articleDescription'] = blocks.body[0].bodyTextSummary;
                obj['articleSection'] = section;
                obj['articleDate'] = date;
                obj['shareUrl'] = shareUrl;
                let image = '';
                if (blocks.main == null || blocks.main.elements == null ||
                    blocks.main.elements[0].assets == null) {
                    image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                } else {
                    let assets = result.blocks.main.elements[0].assets;
                    if (assets[assets.length - 1] == null ||
                        assets[assets.length - 1].file == null ||
                        assets[assets.length - 1].file === '') {
                        image = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
                    } else {
                        image = assets[assets.length - 1].file;
                    }
                }
                obj['articleImage'] = image;
                returnList.push(obj);
                cnt++;
            }

            res.send({results: returnList});
        })
        .catch(function (error) {
            console.error(error);
        });
});

/* *********************************************** */
// NY Times URL for Home tab: /nytimes
// NY Times URL for other tabs: /nytimes?section=
app.get('/nytimes', function (req, res) {
    let url = '';
    if (req.query.section == null) {
        url = 'https://api.nytimes.com/svc/topstories/v2/home.json?api-key=' + NYTIMES_KEY;
    } else {
        url = 'https://api.nytimes.com/svc/topstories/v2/' + req.query.section + '.json?api-key=' + NYTIMES_KEY;
    }
    axios.get(url)
        .then(function (response) {
            let results = response.data.results;
            let returnList = [];
            let cnt = 0;

            for (let i = 0; i < results.length && cnt < 10; i++) {
                let result = results[i];
                let obj = {};
                let id = result.url;
                let title = result.title;
                let section = result.section;
                let date = result.published_date.slice(0, 10);
                let description = result.abstract;
                let shareUrl = result.url;
                let multimedia = result.multimedia;
                if (title == null || title === '' || section == null || section === '' ||
                    date == null || date === '' || multimedia == null || multimedia === '' ||
                    shareUrl == null || shareUrl === '' || id == null || id === '' ||
                    description == null || description === '') {
                    continue;
                }
                obj['id'] = id;
                obj['articleTitle'] = title;
                obj['articleDescription'] = description;
                obj['articleSection'] = section;
                obj['articleDate'] = date;
                obj['shareUrl'] = shareUrl;
                let image = '';
                let useDefault = true;
                for (let i = 0; i < multimedia.length; i++) {
                    if (multimedia[i].width >= 2000) {
                        image = multimedia[i].url;
                        useDefault = false;
                        break;
                    }
                }
                if (useDefault === true) {
                    image = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
                }
                obj['articleImage'] = image;
                returnList.push(obj);
                cnt++;
            }

            res.send({results: returnList});
        })
        .catch(function (error) {
            console.error(error);
        });
});

/* *********************************************** */
// For article detail: /article?id=
app.get('/article', function (req, res) {
    let url = '';
    let image;
    let returnObj = {};
    if (req.query.id.slice(0, 8) !== 'https://') {
        // Guardian
        url = 'https://content.guardianapis.com/' + req.query.id + '?api-key=' + GUARDIAN_KEY + '&show-blocks=all';
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
                returnObj['title'] = data.webTitle;
                returnObj['description'] = data.blocks.body[0].bodyTextSummary.replace(/(?:\r\n|\r|\n)/g, '<br><br>');
                returnObj['date'] = data.webPublicationDate.slice(0, 10);
                returnObj['section'] = data.sectionId;
                returnObj['shareUrl'] = data.webUrl;

                res.send(returnObj);
            })
            .catch(function (error) {
                console.error(error);
            });
    } else {
        // NY Times
        url = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?fq=web_url:("' + req.query.id
            + '")&api-key=' + NYTIMES_KEY;
        axios.get(url)
            .then(function (response) {
                let data = response.data.response.docs[0];
                let articleImageList = data.multimedia;
                let useDefault = true;
                for (let i = 0; i < articleImageList.length; i++) {
                    if (articleImageList[i].width >= 2000) {
                        image = 'https://www.nytimes.com/' + articleImageList[i].url;
                        useDefault = false;
                        break;
                    }
                }
                if (useDefault === true) {
                    image = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
                }
                returnObj['image'] = image;
                returnObj['title'] = data.headline.main;
                returnObj['description'] = data.abstract;
                returnObj['date'] = data.pub_date.slice(0, 10);
                returnObj['section'] = data.news_desk;
                returnObj['shareUrl'] = data.web_url;
                res.send(returnObj);
            })
            .catch(function (error) {
                console.error(error);
            });
    }
});

/* *********************************************** */
// For article search: /search?q=
app.get('/search', function (req, res) {
    let resultObj = {};
    let urlOne = 'https://content.guardianapis.com/search?q=' + req.query.q + '&api-key=' + GUARDIAN_KEY + '&show-blocks=all';
    let urlTwo = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?q=' + req.query.q + '&api-key=' + NYTIMES_KEY;
    const requestOne = axios.get(urlOne);
    const requestTwo = axios.get(urlTwo);
    axios.all([requestOne, requestTwo]).then(axios.spread((...responses) => {
        const responseOne = responses[0].data.response.results.slice(0,5);
        const responseTwo = responses[1].data.response.docs.slice(0,5);
        // use/access the results
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
            obj["date"] = item.webPublicationDate.slice(0, 10);
            obj["section"] = item.sectionId;
            obj["source"] = "Guardian";
            obj["shareUrl"] = item.webUrl;
            resultOne.push(obj);
        });

        let resultTwo = [];
        responseTwo.forEach(item => {
            let obj = {};
            let articleImageList = item.multimedia;
            let useDefault = true;
            for (let i = 0; i < articleImageList.length; i++) {
                if (articleImageList[i].width >= 2000) {
                    obj["image"] = 'https://www.nytimes.com/' + articleImageList[i].url;
                    useDefault = false;
                    break;
                }
            }
            if (useDefault === true) {
                obj["image"] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
            }
            obj["id"] = item.web_url;
            obj["title"] = item.headline.main;
            obj["date"] = item.pub_date.slice(0, 10);
            obj["section"] = item.news_desk;
            obj["source"] = "NYTimes";
            obj["shareUrl"] = item.web_url;
            resultOne.push(obj);
        });

        resultObj = resultOne.concat(resultTwo);
        res.send(resultObj);
    })).catch(errors => {
        console.error(errors);
    })

});
app.listen(process.env.PORT || 8080);