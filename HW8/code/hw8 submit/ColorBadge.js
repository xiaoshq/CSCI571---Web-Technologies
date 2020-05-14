import React, {Component} from 'react';
import {Badge} from "react-bootstrap";
import './ColorBadge.css'

class ColorBadge extends Component{
    constructor(props) {
        super(props);
    }
    render() {
        let section = this.props.sectionName;
        if (section == null || section === '') {
            section = 'none';
        }
        if (section === 'sport') {
            section = 'sports';
        }

        let className = 'other';
        if (section === 'world' || section === 'politics' || section === 'business' || section === 'technology' || section === 'sports' || section === 'Guardian' || section === 'NYTimes') {
            className = section;
        }
        section = section.toUpperCase();
        return(
            <Badge className={className}>{section}</Badge>
        );
    }
}

export default ColorBadge;