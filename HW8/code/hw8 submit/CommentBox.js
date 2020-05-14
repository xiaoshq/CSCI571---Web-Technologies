import React, {Component} from 'react';
import commentBox from 'commentbox.io';
import './CommentBox.css'

class CommentBox extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.removeCommentBox = commentBox('5744465994055680-proj');
    }

    componentWillUnmount() {
        this.removeCommentBox();
    }

    render() {

        return (
            <div className="commentbox" id={this.props.id}/>
        );
    }
}

export default CommentBox;