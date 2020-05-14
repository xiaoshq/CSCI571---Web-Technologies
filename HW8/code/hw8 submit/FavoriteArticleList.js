import React, {Component} from 'react';
import FavoriteArticle from "./FavoriteArticle";
import {withRouter} from "react-router-dom";
import { compose } from 'recompose';
import './FavoriteArticle.css';
import {Container, Row, Col} from "react-bootstrap";
import { css } from "@emotion/core";
import BounceLoader from "react-spinners/BounceLoader";

const FavoriteArticleWrapper = compose(
    withRouter
)(FavoriteArticle);

const override = css`
  display: block;
  margin: 0 auto;
`;

class FavoriteArticleList extends Component{
    constructor(props) {
        super(props);
        this.state = {
            articleList: [],
            loading: true
        };
        this.getData = this.getData.bind(this);
        this.updateInfo = this.updateInfo.bind(this);
    }
    componentDidMount() {
        let favorites = JSON.parse(localStorage.getItem('favorites'));
        let list = [];
        Object.keys(favorites).forEach(function(key) {
            list.push(favorites[key]);
        });
        this.setState({
            articleList: list,
            loading: false,
            favorites: localStorage.getItem(('favorites'))
        })
    }
    updateInfo() {
        let favorites = JSON.parse(localStorage.getItem('favorites'));
        let list = [];
        Object.keys(favorites).forEach(function(key) {
            list.push(favorites[key]);
        });
        this.setState({
            articleList: list,
            loading: false,
            favorites: localStorage.getItem(('favorites'))
        })
    }
    componentDidUpdate(prevProps, prevState) {
        if (prevState.favorites !== this.state.favorites) {
            this.updateInfo();
        }
    }

    getData(data) {
        this.setState({favorites: data});
    }

    render() {
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
        let noArticle;
        if (this.state.articleList === undefined || this.state.articleList.length === 0) {
            noArticle = <div className="no-article">You have no saved articles</div>;
        }

        return (
            <Container fluid className="favorite-article-list">
                {spinner}
                {noArticle}
                <Row>
                    {this.state.articleList &&
                    this.state.articleList.length > 0 &&
                    this.state.articleList.map((item, index) => {
                        return (
                            <Col md={3} xs={12} key={item.id} className="favorite-article-list-col">
                                <FavoriteArticleWrapper articleInfo={item} sendData={this.getData}/>
                            </Col>
                        )
                    })
                    }
                </Row>
            </Container>
        );
    }
}

export default FavoriteArticleList;