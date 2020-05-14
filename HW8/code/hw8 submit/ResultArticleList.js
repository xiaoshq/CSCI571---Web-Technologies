import React, {Component} from 'react';
import ResultArticle from "./ResultArticle";
import {withRouter} from "react-router-dom";
import { compose } from 'recompose';
import {Container, Row, Col} from "react-bootstrap";
import { css } from "@emotion/core";
import BounceLoader from "react-spinners/BounceLoader";
import './ResultArticle.css';

const ResultArticleWrapper = compose(
    withRouter
)(ResultArticle);

const override = css`
  display: block;
  margin: 0 auto;
`;

class ResultArticleList extends Component{
    constructor(props) {
        super(props);
        this.state = {
            articleList: [],
            loading: true
        };
        this.updateInfo = this.updateInfo.bind(this);
    }

    componentDidMount() {
        this.updateInfo();
    }

    updateInfo() {
        let url = 'http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/search?q=' + this.props.location.state.q;
        this.setState({
            articleList: [],
            loading: true
        });
        fetch(url)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Something went wrong ...');
                }
            })
            .then(data => {
                this.setState({
                    articleList: data,
                    loading: false
                });
                console.log(this.state.articleList);
            })
            .catch(error => console.log(error));
    }

    componentDidUpdate(prevProps) {
        if (this.props.location.state.q !== prevProps.location.state.q) {
            this.updateInfo();
        }
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
        if (this.state.loading === false && (this.state.articleList === undefined || this.state.articleList.length === 0)) {
            noArticle = <div className="no-article">No results</div>;
        }
        return (
            <Container fluid className="result-article-list">
                {spinner}
                {noArticle}
                <Row>
                    {this.state.articleList &&
                    this.state.articleList.length > 0 &&
                    this.state.articleList.map((item, index) => {
                        return (
                            <Col md={3} xs={12} key={item.id} className="result-article-list-col">
                                <ResultArticleWrapper articleInfo={item}/>
                            </Col>
                        )
                    })
                    }
                </Row>
            </Container>
        );
    }
}

export default ResultArticleList;