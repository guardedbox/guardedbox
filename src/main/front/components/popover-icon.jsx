import React, { Component } from 'react';
import { Popover, PopoverBody } from 'reactstrap';
import Octicon from '@primer/octicons-react'

class PopoverIcon extends Component {

    state = {
        open: false
    }

    constructor(props) {

        super(props);
        this.id = 'popover-icon-' + Math.random().toString().substr(2);

    }

    render() {

        return (

            <span
                id={this.props.id || this.id}
                onClick={() => { this.setState({ open: true }) }}
                className={this.props.className}
                style={{ ...this.props.style, ...{ cursor: 'pointer' } }}>

                <Octicon icon={this.props.icon} />

                {this.props.popoverText ?
                    <Popover
                        target={this.props.id || this.id}
                        trigger="legacy"
                        placement={this.props.placement}
                        isOpen={this.state.open}
                        toggle={() => { this.setState({ open: false }) }}>
                        <PopoverBody>{this.props.popoverText}</PopoverBody>
                    </Popover>
                    : null
                }

            </span>

        );

    }

}

export default PopoverIcon;
