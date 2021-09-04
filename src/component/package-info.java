/**
 * Defines two hierarchies, the {@link component.components.Component
 * Components} and the {@link component.graphics.ComponentGraphic Graphics} in
 * the {@link component.components components} and the {@link component.graphics
 * graphics} packages respectively, as well as some Exceptions regarding these
 * hierarchies in the {@link component.exceptions exceptions} package.
 * <p>
 * {@code Components} represent parts of a logic circuit (input/output pins,
 * logic gates and the connections between them) and can be combined to create a
 * fully functional logic circuit. The only known limitation is JVM's call stack
 * size which directly limits the number of times a single signal can be
 * transmitted, since the transmission from one {@code Component} to the next
 * corresponds to exactly one method call (the circuit inside composite Gates
 * also accounts for one method call per transmission).
 * <p>
 * {@code Graphics} provide a way to interact with {@code Components} using the
 * javax.swing library. They can only be created using another {@code Component}
 * and once created any interactions the user takes on the {@code Graphic} will
 * be reflected in the {@code Component's} state and vice versa, changes to the
 * {@code Component} update its {@code Graphic}.
 * <p>
 * Both {@code Components} and {@code Graphics} belong to a specific
 * {@link component.ComponentType Type}.
 */
package component;
