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
                className={'action-icon' + (this.props.className ? ' ' + this.props.className : '')}
                style={this.props.style}>

                <Octicon icon={this.props.icon} />

                {this.props.popoverText == null ? null :
                    < Popover
                        target={this.props.id || this.id}
                        trigger="legacy"
                        placement={this.props.placement}
                        isOpen={this.state.open}
                        toggle={() => { this.setState({ open: false }) }}>
                        <PopoverBody>{this.props.popoverText}</PopoverBody>
                    </Popover>
                }

                {this.props.badgeText == null ? null :
                    <Badge className="badge-notification" color={this.props.badgeColor}>
                        {this.props.badgeText}
                    </Badge>
                }

            </span>

        );

    }

}

export default PopoverIcon;
