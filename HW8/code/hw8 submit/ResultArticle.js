import React, {Component} from 'react';
import {Card} from "react-bootstrap";
import ShareModal from "../ShareModal/ShareModal";
import { MdDelete } from "react-icons/md";
import ColorBadge from "../ColorBadge/ColorBadge";
import './ResultArticle.css'

class ResultArticle extends Component{
    constructor(props) {
        super(props);
        this.state = {
        };
        this.showArticle = this.showArticle.bind(this);
        this.handleChildClick = this.handleChildClick.bind(this);
    }
    showArticle() {
        let articleId = this.props.articleInfo.id;
        let articleSource = this.props.articleInfo.source;
        this.props.history.push({pathname:'/article', search: '?id=' + articleId, state:{articleId: articleId, source: articleSource}});
    }
    handleChildClick(event) {
        event.stopPropagation();
    }
    render() {
        let title = this.props.articleInfo.title;
        let image = this.props.articleInfo.image;
        let date = this.props.articleInfo.date;
        let section = this.props.articleInfo.section;
        return(
            <Card id="result-article-card" onClick={this.showArticle}>
                <Card.Body>
                    <Card.Title id="result-article-title">
                        <span>{title}</span>
                        &nbsp;
                        <div onClick={this.handleChildClick} style={{display:'inline'}}>
                            <ShareModal id="default-article-share" title={title} url={this.props.articleInfo.shareUrl}/>
                        </div>
                    </Card.Title>
                    <div id="result-article-image-part">
                        <Card.Img src={image} id="result-article-image"/>
                    </div>
                    <div id="result-article-widget">
                        <Card.Text id="result-article-date">
                            {date}
                        </Card.Text>
                        <div id="result-article-badge">
                            <ColorBadge sectionName={section}/>
                        </div>
                    </div>
                </Card.Body>
            </Card>
        );
    }
}

export default ResultArticle;