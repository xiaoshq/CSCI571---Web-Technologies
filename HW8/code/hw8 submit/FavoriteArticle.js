import React, {Component} from 'react';
import {Card} from "react-bootstrap";
import ShareModal from "../ShareModal/ShareModal";
import { MdDelete } from "react-icons/md";
import ColorBadge from "../ColorBadge/ColorBadge";
import './FavoriteArticle.css'
import {toast} from "react-toastify";

class FavoriteArticle extends Component{
    constructor(props) {
        super(props);
        this.state = {
        };
        this.showArticle = this.showArticle.bind(this);
        this.handleChildClick = this.handleChildClick.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
    }
    showArticle() {
        let articleId = this.props.articleInfo.id;
        let articleSource = this.props.articleInfo.source;
        this.props.history.push({pathname:'/article', search: '?id=' + articleId, state:{articleId: articleId, source: articleSource}});
    }
    handleChildClick(event) {
        event.stopPropagation();
    }
    handleDelete(event) {
        event.stopPropagation();
        let id = this.props.articleInfo.id;
        localStorage.removeItem(id.toString());
        let favoritesObj = JSON.parse(localStorage.getItem('favorites'));
        delete favoritesObj[id.toString()];
        localStorage.setItem('favorites', JSON.stringify(favoritesObj));
        toast("Removing " + this.props.articleInfo.title, {
            className: "toastMsg",
            position: "top-center",
            autoClose: 3000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: false,
            draggable: true
        });
        this.props.sendData(JSON.stringify(favoritesObj));
    }
    render() {
        let title = this.props.articleInfo.title;
        let image = this.props.articleInfo.image;
        let date = this.props.articleInfo.date;
        let section = this.props.articleInfo.section;
        let source = this.props.articleInfo.source;
        let url = this.props.articleInfo.shareUrl;
        return(
            <Card id="favorite-article-card" onClick={this.showArticle}>
                <Card.Body>
                    <Card.Title id="favorite-article-title">
                        <span>{title}</span>
                        &nbsp;
                        <div onClick={this.handleChildClick} style={{display:'inline'}}>
                            <ShareModal id="default-article-share" url={url} title={source} subtitle={title}/>
                        </div>
                        <div onClick={this.handleDelete} style={{display:'inline'}}>
                            <MdDelete/>
                        </div>
                    </Card.Title>
                    <div id="favorite-article-image-part">
                        <Card.Img src={image} id="favorite-article-image"/>
                    </div>
                    <div id="favorite-article-widget">
                        <Card.Text id="favorite-article-date">
                            {date}
                        </Card.Text>
                        <div id="favorite-article-badge">
                            <ColorBadge sectionName={section}/>
                            &nbsp;
                            <ColorBadge sectionName={source}/>
                        </div>
                    </div>
                </Card.Body>
            </Card>
        );
    }
}

export default FavoriteArticle;