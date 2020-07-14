import React, { Component } from 'react';
import { Button } from 'reactstrap';
import { UncontrolledTooltip } from 'reactstrap';
import Octicon from '@primer/octicons-react'

class ButtonIcon extends Component {

    constructor(props) {

        super(props);
        this.id = 'button-icon-' + Math.random().toString().substr(2);

    }

    render() {

        return (

            <Button
                id={this.props.id || this.id}
                onClick={this.props.onClick}
                type={this.props.type}
                form={this.props.form}
                disabled={this.props.disabled}
                color={this.props.color}
                size={this.props.size}
                className={this.props.className}
                style={this.props.style}>

                <Octicon icon={this.props.icon} />

                {this.props.tooltipText == null ? null :
                    <UncontrolledTooltip placement={this.props.tooltipPlacement || "top"} target={this.props.id || this.id}>
                        {this.props.tooltipText}
                    </UncontrolledTooltip>
                }

            </Button>

        );

    }

}

export default ButtonIcon;
