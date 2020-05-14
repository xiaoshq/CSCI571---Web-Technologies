import React, {Component} from "react";
import {Navbar} from "react-bootstrap";
import Switch from "react-switch";
import './NavigationBar.css';

class SourceSwitch extends Component{
    constructor(props) {
        super(props);
        if (localStorage.getItem('checked') === 'true') {
            this.state = {checked: true};
        } else { // 'false' or ''
            this.state = {checked: false};
        }
        this.handleChange = this.handleChange.bind(this);
    }
    handleChange(checked) {
        this.setState({ checked });
        if (localStorage.getItem('source') === 'Guardian') {
            localStorage.setItem('source', 'NYTimes');
            localStorage.setItem('checked', 'false');
        } else {
            localStorage.setItem('source', 'Guardian');
            localStorage.setItem('checked', 'true');
        }
        window.location.reload(false);
    }
    render() {
        return(
            <>
                <span className="source-name">NYTimes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                <Switch
                    checked={this.state.checked}
                    onChange={this.handleChange}
                    onColor="#4696EC" // 86d3ff
                    offColor="#DCDDDC"
                    onHandleColor="#ffffff" // 2693e6
                    offHandleColor="#ffffff"
                    handleDiameter={18}
                    uncheckedIcon={false}
                    checkedIcon={false}
                    height={20}
                    width={48}
                    className="react-switch"
                    id="material-switch"
                />
                <span className="source-name">Guardian</span>
            </>
        );
    }
}

export default SourceSwitch;