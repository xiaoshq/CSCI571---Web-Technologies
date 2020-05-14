import React, {Component} from "react";
import {Navbar, Nav, Container} from "react-bootstrap";
import { FaRegBookmark, FaBookmark } from "react-icons/fa";
import ReactTooltip from 'react-tooltip'
import Switch from "react-switch";
import './NavigationBar.css';
import Selection from "./Selection";
import SourceSwitch from "./SourceSwitch";
import {withRouter, NavLink} from "react-router-dom";

class NavigationBar extends Component{
    constructor(props) {
        super(props);
        if (localStorage.getItem('checked') === 'true') {
            this.state = {checked: true};
        } else { // 'false' or ''
            this.state = {checked: false};
        }
        this.state = {
            toClear: false
        };
        this.clearValue = this.clearValue.bind(this);
        this.callbackFunction = this.callbackFunction.bind(this);
    }
    clearValue() {
        this.setState({toClear: true});
    }

    callbackFunction = (childData) => {
        this.setState({toClear: childData})
    };

    render() {
        let sectionSwitch = (window.location.pathname === '/article' ||
            window.location.pathname === '/favorites' ||window.location.pathname === '/search');
        return (
            <Navbar variant="dark" expand="lg" id="navbar">
                <Selection toClear={this.state.toClear} parentCallback = {this.callbackFunction}/>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="mr-auto">
                        <NavLink to="/" className="navlink" activeClassName="selected" exact={true} onClick={this.clearValue}>
                            Home
                        </NavLink>
                        <NavLink to="/world" className="navlink" activeClassName="selected" onClick={this.clearValue}>
                            World
                        </NavLink>
                        <NavLink to="/politics" className="navlink" activeClassName="selected" onClick={this.clearValue}>
                            Politics
                        </NavLink>
                        <NavLink to="/business" className="navlink" activeClassName="selected" onClick={this.clearValue}>
                            Business
                        </NavLink>
                        <NavLink to="/technology" className="navlink" activeClassName="selected" onClick={this.clearValue}>
                            Technology
                        </NavLink>
                        <NavLink to="/sports" className="navlink" activeClassName="selected" onClick={this.clearValue}>
                            Sports
                        </NavLink>
                    </Nav>
                    <Nav id="navbar-right">
                        <NavLink to="/favorites" id="favorites">
                                <div id="favorites-bookmark-empty"
                                     className={`${window.location.pathname === '/favorites' ? "detailed-bookmark-hide" : "detailed-bookmark-show"}`}>
                                    <FaRegBookmark className="favorite-bookmark-icon"
                                                   onClick={this.handleBookmark}
                                                   data-tip data-for='navbar-bookmark'/>
                                </div>
                                <div id="favorites-bookmark-full"
                                     className={`${window.location.pathname === '/favorites' ? "detailed-bookmark-show" : "detailed-bookmark-hide"}`}>
                                    <FaBookmark className="favorite-bookmark-icon"
                                                onClick={this.handleBookmark}
                                                data-tip data-for='navbar-bookmark'/>
                                </div>
                            <ReactTooltip id="navbar-bookmark" place="bottom" type="dark" effect="solid">
                                <span>Bookmark</span>
                            </ReactTooltip>
                        </NavLink>
                        {sectionSwitch ? <div/> : <SourceSwitch />}
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default withRouter(NavigationBar);