import React, {Component} from "react";
import {Card, Row, Col, Container} from "react-bootstrap";
import ShareModal from "../ShareModal/ShareModal";
import ColorBadge from "../ColorBadge/ColorBadge";
import './DefaultArticle.css';

class DefaultArticle extends Component {
    constructor(props) {
        super(props);
        this.state = {
            source: this.props.source
        };
        this.showArticle = this.showArticle.bind(this);
        this.handleChildClick = this.handleChildClick.bind(this);
    }

    showArticle() {
        let articleId = this.props.articleInfo.id;
        this.props.history.push({
            pathname: '/article',
            search: '?id=' + articleId,
            state: {articleId: articleId, source: this.state.source}
        });
    }

    handleChildClick(event) {
        event.stopPropagation();
    }

    render() {
        return (
            <Card id="default-article-card" onClick={this.showArticle}>
                <Card.Body>
                    <Row>
                        <Col md={3}>
                            <Container id="default-article-image-part" fluid>
                                <Card.Img src={this.props.articleInfo.articleImage} id="default-article-image"/>
                            </Container>
                        </Col>
                        <Col md={9}>
                            <div id="default-article-text">
                                <Card.Title className="default-article-title">{this.props.articleInfo.articleTitle}
                                    <div onClick={this.handleChildClick} style={{display: 'inline'}}>
                                        <ShareModal id="default-article-share"
                                                    url={this.props.articleInfo.shareUrl}
                                                    title={this.props.articleInfo.articleTitle}/>
                                    </div>
                                </Card.Title>
                                <Card.Text className="default-article-description">
                                    {this.props.articleInfo.articleDescription}
                                </Card.Text>
                                <div id="default-article-widget">
                                    <Card.Text id="default-article-date">
                                        {this.props.articleInfo.articleDate}
                                    </Card.Text>
                                    <div id="default-article-badge">
                                        <ColorBadge sectionName={this.props.articleInfo.articleSection}/>
                                    </div>
                                </div>
                            </div>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>
        );
    }
}

export default DefaultArticle;