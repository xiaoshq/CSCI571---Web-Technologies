import React, { Component } from "react";
import { BrowserRouter, Route, Switch, withRouter } from 'react-router-dom';
import { compose } from 'recompose';

import './NavigationPage.css';
import NavigationBar from "../NavigationBar/NavigationBar";
import DefaultArticleList from "../DefaultArticle/DefaultArticleList";
import DetailedArticle from "../DetailedArticle/DetailedArticle";
import FavoriteArticleList from "../FavoriteArticle/FavoriteArticleList";
import ResultArticleList from "../ResultArticle/ResultArticleList";
import ResultArticle from "../ResultArticle/ResultArticle";

class NavigationPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            hasSwitch: true
        };
        this.getHasSwitch = this.getHasSwitch.bind(this);
    }

    getHasSwitch(data) {
        this.setState({hasSwitch: data});
    }

    render() {
        return(
            <BrowserRouter>
                <NavigationBar sendHasSwtich={this.getHasSwitch}/>
                <Switch>
                    <Route exact path='/' component={() => <DefaultArticleList section={"home"}/>}/>
                    <Route exact path='/world' component={() => <DefaultArticleList section={"world"}/>}/>
                    <Route exact path='/politics' component={() => <DefaultArticleList section={"politics"}/>} />
                    <Route exact path='/business' component={() => <DefaultArticleList section={"business"}/>} />
                    <Route exact path='/technology' component={() => <DefaultArticleList section={"technology"}/>} />
                    <Route exact path='/sports' component={() => <DefaultArticleList section={"sports"}/>} />
                    <Route exact path='/article' component={DetailedArticle} />
                    <Route exact path='/favorites' component={FavoriteArticleList} />
                    <Route exact path='/search' component={ResultArticleList} />
                </Switch>
            </BrowserRouter>
        );
    }
}

export default NavigationPage;