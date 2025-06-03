CREATE TABLE orders.orders (
    id_order VARCHAR(36) NOT NULL,
    id_account VARCHAR(36) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    CONSTRAINT pk_order PRIMARY KEY (id_order)
);

CREATE TABLE orders.order_items (
    id_order_item VARCHAR(36) NOT NULL,
    id_order VARCHAR(36) NOT NULL,
    id_product VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (id_order) REFERENCES orders.orders(id_order)
);
