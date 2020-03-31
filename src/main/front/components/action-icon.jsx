import React, { Component } from 'react';
import { UncontrolledTooltip } from 'reactstrap';
import Octicon from '@primer/octicons-react'

class ActionIcon extends Component {

    constructor(props) {

        super(props);
        this.id = 'action-icon-' + Math.random().toString().substr(2);

    }

    render() {

        return (

            <span
                id={this.id}
                onClick={this.props.onClick}
                className={this.props.className}
                style={{ ...this.props.style, ...{ cursor: 'pointer' } }}>

                <Octicon icon={this.props.icon} />

                {this.props.tooltipText ?
                    <UncontrolledTooltip placement="top" target={this.id}>
                        {this.props.tooltipText}
                    </UncontrolledTooltip>
                    : null
                }

            </ span>

        );

    }

}

export default ActionIcon;
