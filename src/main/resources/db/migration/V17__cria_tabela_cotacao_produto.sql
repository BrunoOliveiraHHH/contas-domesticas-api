create table cotacao_produto (
    id             bigint generated always as identity,
    produto_id     bigint       not null,
    mercado_id     bigint       not null,
    preco_unitario numeric(15, 2) not null,
    data           date         not null,
    origem         varchar(10)  not null,
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_cotacao_produto primary key (id),
    constraint fk_cotacao_produto foreign key (produto_id) references produto (id),
    constraint fk_cotacao_mercado foreign key (mercado_id) references mercado (id)
);

create index ix_cotacao_produto_mercado on cotacao_produto (produto_id, mercado_id);
