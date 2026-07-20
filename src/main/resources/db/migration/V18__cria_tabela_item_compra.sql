create table item_compra (
    id                   bigint generated always as identity,
    lista_compra_id      bigint       not null,
    produto_id           bigint       not null,
    quantidade           numeric(12, 3) not null,
    unidade_medida_id    bigint,
    mercado_escolhido_id bigint,
    preco_unitario       numeric(15, 2),
    comprado             boolean      not null default false,
    criado_em            timestamptz  not null,
    criado_por           varchar(100) not null,
    atualizado_em        timestamptz,
    atualizado_por       varchar(100),
    constraint pk_item_compra primary key (id),
    constraint fk_item_compra_lista foreign key (lista_compra_id) references lista_compra (id) on delete cascade,
    constraint fk_item_compra_produto foreign key (produto_id) references produto (id),
    constraint fk_item_compra_unidade_medida foreign key (unidade_medida_id) references unidade_medida (id),
    constraint fk_item_compra_mercado foreign key (mercado_escolhido_id) references mercado (id)
);

create index ix_item_compra_lista on item_compra (lista_compra_id);
