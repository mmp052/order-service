CREATE TABLE orders.orders (
    id_order   VARCHAR(36)    NOT NULL PRIMARY KEY,
    order_date       TIMESTAMP      NOT NULL,
    total      DECIMAL(10,2)  NOT NULL,
    id_account    VARCHAR(36)    NOT NULL
);

CREATE TABLE orders.order_items (
    id_order_item VARCHAR(36)    NOT NULL PRIMARY KEY,
    id_order      VARCHAR(36)    NOT NULL,
    id_product    VARCHAR(36)    NOT NULL,
    quantity      INT            NOT NULL,
    total         DECIMAL(10,2)  NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (id_order)
        REFERENCES orders.orders(id_order)
);