import React, {Component} from 'react';
import {Modal, Button} from 'react-bootstrap';
import {MdShare} from "react-icons/md";
import {
    EmailIcon,
    EmailShareButton, FacebookIcon,
    FacebookShareButton, TwitterIcon,
    TwitterShareButton,
} from "react-share";
import '../ShareModal/ShareModal.css'

class ShareModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            show: false
        };
        this.modal = this.modal.bind(this);
    }

    modal() {
        const handleClose = () => {
            this.setState(
                {
                    show: false
                }
            )
        };
        const handleShow = () => {
            this.setState(
                {
                    show: true
                }
            )
        };

        return (
            <>
                <MdShare onClick={handleShow}></MdShare>

                <Modal show={this.state.show} onHide={handleClose}>
                    <Modal.Header closeButton>
                        <Modal.Title className="modal-title">{this.props.title}<br/><small>{this.props.subtitle}</small></Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <div className="share-button-text">Share via</div>
                        <div className="share-button-group">
                            <FacebookShareButton url={this.props.url}
                                                 hashtag="#CSCI_571_NewsApp"
                                                 className="facebook-share-button">
                                <FacebookIcon size={60} round/>
                            </FacebookShareButton>
                            <TwitterShareButton url={this.props.url}
                                                hashtags={["CSCI_571_NewsApp"]}
                                                className="twitter-share-button">
                                <TwitterIcon size={60} round/>
                            </TwitterShareButton>
                            <EmailShareButton url={this.props.url}
                                              subject="#CSCI_571_NewsApp"
                                              className="email-share-button">
                                <EmailIcon size={60} round/>
                            </EmailShareButton>
                        </div>
                    </Modal.Body>
                </Modal>
            </>
        );
    }

    render() {
        return (
            <this.modal/>
        );
    }
}

export default ShareModal;