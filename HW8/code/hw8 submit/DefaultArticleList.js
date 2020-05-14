import React, { Component } from "react";
import DefaultArticle from "./DefaultArticle";
import './DefaultArticle.css';
import {Container, Row, Col} from "react-bootstrap";
import {withRouter} from "react-router-dom";
import { compose } from 'recompose';
import ShareModal from "../ShareModal/ShareModal";
import BounceLoader from "react-spinners/BounceLoader";
import {css} from "@emotion/core";

const DefaultArticleWrapper = compose(
    withRouter
)(DefaultArticle);

const override = css`
  display: block;
  margin: 0 auto;
`;

class DefaultArticleList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            source: localStorage.getItem('source'),
            section: window.location.pathname,
            loading: true,
            articleList: []
        };
        this.updateInfo = this.updateInfo.bind(this);
    }

    updateInfo() {
        let url;
        if (this.state.section === '/') {
            if (localStorage.getItem('source') === 'Guardian') {
                url = 'http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/guardian';
            } else {
                url = 'http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/nytimes';
            }
        } else {
            let sectionName = this.state.section.slice(1, this.state.section.length);
            if (sectionName === 'sports' && this.state.source === 'Guardian') {
                sectionName = 'sport';
            }
            if (localStorage.getItem('source') === 'Guardian') {
                url = 'http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/guardian?section=' + sectionName;
            } else {
                url = 'http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/nytimes?section=' + sectionName;
            }
        }
        console.log(url);
        fetch(url)
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Something went wrong ...');
                }
            })
            .then(data => {
                let results = data.results;
                this.setState({
                    articleList: results,
                    loading: false
                });
                console.log(this.state.articleList);
            })
            .catch(error => console.log(error));
    }

    componentDidMount() {
        this.updateInfo();
    }

    componentDidUpdate(prevProps: Readonly<P>, prevState: Readonly<S>, snapshot: SS): void {
        if (this.props.section !== prevProps.section) {
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

        return (
            <Container fluid className="default-article-list">
                {spinner}
                {this.state.articleList &&
                this.state.articleList.length > 0 &&
                this.state.articleList.map((item, index) => {
                    return (
                        <Row key={item.id}>
                            <DefaultArticleWrapper articleInfo={item} source={this.state.source}/>
                        </Row>
                    )
                })
                }
            </Container>
        );
    }
}

export default DefaultArticleList;