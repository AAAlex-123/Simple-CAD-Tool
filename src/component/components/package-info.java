/**
 * Defines a set of components that can fully define any logic circuit. The
 * client can interact with objects of type {@link components.Component
 * Component} which represent a circuit component that can be connected to other
 * Components and transmit a signal. Components can also be drawn onto the
 * screen.
 * <p>
 * Since none of the circuit-related methods of the Components are visible, the
 * client may use the static methods provided by the
 * {@link components.ComponentFactory ComponentFactory} class to create, delete
 * and connect Components to one another, to give input and to get output.
 */
package component.components;
