import React, { Component } from 'react';
import { UncontrolledTooltip, Badge } from 'reactstrap';
import Octicon from '@primer/octicons-react'

class ActionIcon extends Component {

    constructor(props) {

        super(props);
        this.id = 'action-icon-' + Math.random().toString().substr(2);

    }

    render() {

        return (

            <span
                id={this.props.id || this.id}
                onClick={this.props.onClick}
                className={this.props.className}
                style={{ ...this.props.style, ...{ cursor: 'pointer', position: 'relative' } }}>

                <Octicon icon={this.props.icon} />

                {this.props.tooltipText == null ? null :
                    <UncontrolledTooltip placement="top" target={this.props.id || this.id}>
                        {this.props.tooltipText}
                    </UncontrolledTooltip>
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

export default ActionIcon;
