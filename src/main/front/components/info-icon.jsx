import React, { Component } from 'react';
import { UncontrolledTooltip, Popover, PopoverBody, Badge } from 'reactstrap';
import Octicon from '@primer/octicons-react'

class InfoIcon extends Component {

    state = {
        open: false
    }

    constructor(props) {

        super(props);
        this.id = 'info-icon-' + Math.random().toString().substr(2);

    }

    render() {

        return (

            <span
                id={this.props.id || this.id}
                onClick={() => { this.setState({ open: true }) }}
                className={'action-icon' + (this.props.className ? ' ' + this.props.className : '')}
                style={this.props.style}>

                <Octicon icon={this.props.icon} />

                {this.props.tooltipText == null ? null :
                    <UncontrolledTooltip placement={this.props.tooltipPlacement || "top"} target={this.props.id || this.id}>
                        {this.props.tooltipText}
                    </UncontrolledTooltip>
                }

                {this.props.tooltipText == null ? null :
                    <Popover
                        target={this.props.id || this.id}
                        trigger="legacy"
                        placement={this.props.tooltipPlacement || "top"}
                        className="no-tooltip-fallback"
                        isOpen={this.state.open}
                        toggle={() => { this.setState({ open: false }) }}>
                        <PopoverBody>{this.props.tooltipText}</PopoverBody>
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

export default InfoIcon;
