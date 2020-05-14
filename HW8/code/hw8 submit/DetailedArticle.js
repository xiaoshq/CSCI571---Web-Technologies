import React, {Component} from "react";
import {Card, Nav} from "react-bootstrap";
import {MdKeyboardArrowDown, MdKeyboardArrowUp} from "react-icons/md";
import {
    EmailIcon,
    EmailShareButton,
    FacebookIcon,
    FacebookShareButton,
    TwitterIcon,
    TwitterShareButton
} from "react-share";
import { FaRegBookmark, FaBookmark} from "react-icons/fa";
import './DetailedArticle.css';
import ReactTooltip from "react-tooltip";
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import CommentBox from "../CommentBox/CommentBox";
import {css} from "@emotion/core";
import BounceLoader from "react-spinners/BounceLoader";
import * as Scroll from 'react-scroll';

const override = css`
  display: block;
  margin: 0 auto;
`;

class DetailedArticle extends Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            articleDetail: {}
        };
        this.source = this.props.location.state.source;
        this.articleId = this.props.location.state.articleId;
        this.handleBookmark = this.handleBookmark.bind(this);
        this.handleArrowDown = this.handleArrowDown.bind(this);
        this.handleArrowUp = this.handleArrowUp.bind(this);
    }
    componentDidMount() {
        fetch('http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/article?id=' + this.articleId)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Something went wrong ...');
                }
            })
            .then(data => {
                this.setState({
                    loading: false,
                    articleDetail: {
                        articleImage : data.image,
                        articleTitle: data.title,
                        articleDescription: data.description,
                        articleDate: data.date,
                        articleSection: data.section,
                        shareUrl: data.shareUrl
                    }
                });
            })
            .then(() => {
                let description = document.getElementById('detailed-description');
                if (description.clientHeight > 145) {
                    description.className = 'ellipsis';
                    document.getElementById('arrowdown').style.display = 'block';
                }
            })
            .catch(error => console.log(error));
    }

    handleBookmark() {
        let id = this.articleId;
        if (localStorage.getItem(id.toString()) == null) { // not favorite --> favorite
            // change bookmark icon
            document.getElementById('detailed-bookmark-empty').className = 'detailed-bookmark-hide';
            document.getElementById('detailed-bookmark-full').className = 'detailed-bookmark-show';
            // update localStorage
            localStorage.setItem(id.toString(), 'true');
            let favoritesObj = JSON.parse(localStorage.getItem('favorites'));
            let articleObj = {
                id: id,
                title: this.state.articleDetail.articleTitle,
                image: this.state.articleDetail.articleImage,
                date: this.state.articleDetail.articleDate,
                section: this.state.articleDetail.articleSection,
                source: this.source,
                shareUrl: this.state.articleDetail.shareUrl
            };
            favoritesObj[id.toString()] = articleObj;
            localStorage.setItem('favorites', JSON.stringify(favoritesObj));
            toast("Saving " + this.state.articleDetail.articleTitle, {
                className: "toastMsg",
                position: "top-center",
                autoClose: 3000,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: false,
                draggable: true
            });
        } else { // favorite --> not favorite
            // change bookmark icon
            document.getElementById('detailed-bookmark-empty').className = 'detailed-bookmark-show';
            document.getElementById('detailed-bookmark-full').className = 'detailed-bookmark-hide';
            // update localStorage
            localStorage.removeItem(id.toString());
            let favoritesObj = JSON.parse(localStorage.getItem('favorites'));
            delete favoritesObj[id.toString()];
            localStorage.setItem('favorites', JSON.stringify(favoritesObj));
            toast("Removing " + this.state.articleDetail.articleTitle, {
                className: "toastMsg",
                position: "top-center",
                autoClose: 3000,
                hideProgressBar: true,
                closeOnClick: true,
                pauseOnHover: false,
                draggable: true
            });
        }
    }

    handleArrowDown() {
        document.getElementById('detailed-description').className = '';
        document.getElementById('arrowup').style.display = 'block';
        document.getElementById('arrowdown').style.display = 'none';
        Scroll.scroller.scrollTo('scroll-top', {
            duration: 1000,
            delay: 0,
            smooth: true,
            offset: 100, // Scrolls to element + 50 pixels down the page
        });
    }
    handleArrowUp() {
        document.getElementById('detailed-description').className = 'ellipsis';
        document.getElementById('arrowup').style.display = 'none';
        document.getElementById('arrowdown').style.display = 'block';
        Scroll.scroller.scrollTo('scroll-top', {
            duration: 1000,
            delay: 0,
            smooth: true,
            offset: -250, // Scrolls to element + 50 pixels down the page
        });
    }

    render() {
        let detail = this.state.articleDetail;
        let spinner;
        if (this.state.loading === true) {
            spinner =
                <div className="spinner">
                    <BounceLoader
                        css={override}
                        size={35}
                        color={"#123abc"}
                        loading={this.state.loading}
                    />
                    <div>Loading</div>
                </div>;
        }
        let article;
        if (this.state.loading === false) {
            article =
                <div>
                    <Card id="detailed-article-card">
                        <Card.Body>
                            <Card.Title id="detailed-title">{detail.articleTitle}</Card.Title>
                            <div id="detailed-article-widget">
                                <Card.Text className="detailed-article-date">
                                    {detail.articleDate}
                                </Card.Text>
                                <div className="detailed-share-button-group">
                                    <FacebookShareButton url={detail.shareUrl}
                                                         hashtag="#CSCI_571_NewsApp"
                                                         className="facebook-share-button"
                                                         data-tip data-for='facebook-share-button'>
                                        <FacebookIcon size={28} round/>
                                    </FacebookShareButton>
                                    <ReactTooltip id="facebook-share-button" place="top" type="dark" effect="solid">
                                        <span>Facebook</span>
                                    </ReactTooltip>
                                    <TwitterShareButton url={detail.shareUrl}
                                                        hashtags={["CSCI_571_NewsApp"]}
                                                        className="twitter-share-button"
                                                        data-tip data-for='twitter-share-button'>
                                        <TwitterIcon size={28} round/>
                                    </TwitterShareButton>
                                    <ReactTooltip id="twitter-share-button" place="top" type="dark" effect="solid">
                                        <span>Twitter</span>
                                    </ReactTooltip>
                                    <EmailShareButton url={detail.shareUrl}
                                                      subject="#CSCI_571_NewsApp"
                                                      className="email-share-button"
                                                      data-tip data-for='email-share-button'>
                                        <EmailIcon size={28} round/>
                                    </EmailShareButton>
                                    <ReactTooltip id="email-share-button" place="top" type="dark" effect="solid">
                                        <span>Email</span>
                                    </ReactTooltip>
                                </div>
                                <div id="detailed-bookmark">
                                    <div id="detailed-bookmark-empty"
                                         className={`${localStorage.getItem(this.articleId.toString()) == null ? "detailed-bookmark-show" : "detailed-bookmark-hide"}`}>
                                        <FaRegBookmark className="detailed-bookmark-icon"
                                                       onClick={this.handleBookmark}
                                                       data-tip data-for='detailed-bookmark'/>
                                    </div>
                                    <div id="detailed-bookmark-full"
                                         className={`${localStorage.getItem(this.articleId.toString()) == null ? "detailed-bookmark-hide" : "detailed-bookmark-show"}`}>
                                        <FaBookmark className="detailed-bookmark-icon"
                                                    onClick={this.handleBookmark}
                                                    data-tip data-for='detailed-bookmark'/>
                                    </div>
                                </div>
                                <ReactTooltip id="detailed-bookmark" place="top" type="dark" effect="solid">
                                    <span>Bookmark</span>
                                </ReactTooltip>
                            </div>
                            <Card.Img variant="top" src={detail.articleImage}/>
                            <Scroll.Element name="scroll-top"></Scroll.Element>
                            <Card.Text id="detailed-description"
                                       dangerouslySetInnerHTML={{__html: detail.articleDescription}}/>
                            <MdKeyboardArrowDown id="arrowdown" onClick={this.handleArrowDown}/>
                            <MdKeyboardArrowUp id="arrowup" onClick={this.handleArrowUp}/>
                        </Card.Body>
                    </Card>
                    <CommentBox id={this.articleId}/>
                </div>;
        }

        return (
            <>
                {spinner}
                {article}
            </>
        );
    }
}

export default DetailedArticle;