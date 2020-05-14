from flask import Flask, request, jsonify
from newsapi import NewsApiClient

application = Flask(__name__)

newsapi = NewsApiClient(api_key='893d0898e20d4d88a711358fd5b8b0a7')

@application.route('/', methods=['GET'])
def homepage():
    return application.send_static_file("index.html")

@application.route('/init', methods=['GET'])
def get_home_headlines():
    top_headlines = newsapi.get_top_headlines(language='en', page_size=30)
    cnn_headlines = newsapi.get_top_headlines(sources='cnn', language='en', page_size=30)
    fox_headlines = newsapi.get_top_headlines(sources='fox-news', language='en', page_size=30)
    sources = newsapi.get_sources(language='en', country='us')
    word_count = count_frequency(top_headlines)['result']
    return jsonify({'top_headlines': top_headlines, 'cnn_headlines': cnn_headlines, 'fox_headlines': fox_headlines,
                    'sources': sources['sources'], 'word_count' : word_count})

def count_frequency(top_headlines):
    unsorted_result = []
    with open('static/stopwords_en.txt', "r") as stopwords_list:
        stopwords = stopwords_list.read().split('\n')
    articles = top_headlines['articles']
    titles_string = ""
    for article in articles:
        titles_string += " " + article['title']
    wordlist = titles_string.split()
    words = removeStopwords(wordlist, stopwords)
    hash_map = {}
    for element in words:
        # Remove Punctuation
        word = element.replace(",", "")
        word = word.replace(";", "")
        word = word.replace(":", "")
        word = word.lower().capitalize()
        # Word Exist?
        if word in hash_map:
            hash_map[word] = hash_map[word] + 1
        else:
            hash_map[word] = 1
    # smooth
    level = []
    for word in hash_map:
        if hash_map[word] not in level:
            level.append(hash_map[word])
    level.sort()
    level_map = {}
    for x in range(len(level)):
        level_map[level[x]] = x + 1
    for word in hash_map:
        hash_map[word] = level_map[hash_map[word]]

    for word in hash_map:
        dict = {"word" : word, "size" : hash_map[word]}
        unsorted_result.append(dict)
    sort_result = sorted(unsorted_result, key = lambda i: i['size'], reverse=True)
    result = []
    if len(sort_result) > 30:
        sort_result = sort_result[0 : 30]
    return {"result" : sort_result}

def removeStopwords(wordlist, stopwords):
    return [w for w in wordlist if w.lower() not in stopwords]

@application.route('/getSource/<category>', methods=['GET'])
def get_source(category):
    sources = newsapi.get_sources(category=category, language='en', country='us')
    return jsonify({"sources": sources})

@application.route('/getSource', methods=['GET'])
def get_all_source():
    sources = newsapi.get_sources(language='en', country='us')
    return jsonify({"sources": sources})

@application.route('/search', methods=['GET', 'POST'])
def search():
    q = request.form['q']
    from_param = request.form['from_param']
    to = request.form['to']
    source = request.form['source']
    try:
        if source == 'all':
            result = newsapi.get_everything(q=q, from_param=from_param, to=to,
                                            language='en', page_size=30, sort_by='publishedAt')
        else:
            result = newsapi.get_everything(q=q, from_param=from_param, to=to, sources=source,
                                        language='en', page_size=30, sort_by='publishedAt')
        return jsonify(result)
    except Exception as e:
        e_msg = str(e).replace("'", '"')
        return e_msg

if __name__ == '__main__':
    application.run()
