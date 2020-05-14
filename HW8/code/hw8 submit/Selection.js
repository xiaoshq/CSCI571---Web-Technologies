import React, { Component } from 'react';

import AsyncSelect from 'react-select/async';
import debounce from "debounce-promise";
import {withRouter} from "react-router-dom";

import './NavigationBar.css';

type State = {
    inputValue: string,
};

const AUTO_SUGGESTION_KEY = '1fecad371974457aa2c51e407e191f8c';

const filterColors = async (inputValue: string) => {
    try {
        const response = await fetch(
            'https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=' + inputValue,
            {
                headers: {
                    "Ocp-Apim-Subscription-Key": AUTO_SUGGESTION_KEY
                }
            }
        );
        const data = await response.json();
        const resultsRaw = data.suggestionGroups[0].searchSuggestions;
        const results = resultsRaw.map(result => ({value: result.displayText, label: result.displayText}));
        console.log(results);
        return results;
    } catch (error) {
        console.log("error");
    }
};


const promiseOptions = inputValue =>
    new Promise(resolve => {
        resolve(filterColors(inputValue));
    });

class WithCallbacks extends Component<*, State> {
    constructor(props) {
        super(props);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleClear = this.handleClear.bind(this);
    }
    state = {
        inputValue: '',
    };

    handleInputChange = (inputValue: string) => {
        this.setState({
            inputValue: inputValue,
        });
        this.props.parentCallback(false);
        this.props.history.push({pathname:'/search', search: '?q=' + inputValue.value, state: {q: inputValue.value}});
        console.log(inputValue);
        return inputValue;
    };

    handleClear = () => {
       this.setState({
          inputValue: ''
       });
    };

    render() {
        let presentValue = this.state.inputValue;
        console.log(this.props.toClear);
        if (this.props.toClear) {
            presentValue = ''
        } else {
            presentValue = this.state.inputValue;
        }
        return(
            <AsyncSelect
                placeholder="Enter keyword .."
                loadOptions={debounce(promiseOptions, 1000, {leading: true})}
                onChange={this.handleInputChange}
                defaultOptions={[]}
                defaultInputValue=""
                noOptionsMessage={() => "No Match"}
                id="search-input"
                value={presentValue}
            />
        );
    }
}
export default withRouter(WithCallbacks);
