create table lista_compra (
    id             bigint generated always as identity,
    nome           varchar(150) not null,
    tipo           varchar(15)  not null,
    carteira_id    bigint       not null,
    data           date         not null,
    status         varchar(10)  not null,
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_lista_compra primary key (id),
    constraint fk_lista_compra_carteira foreign key (carteira_id) references carteira (id)
);

create index ix_lista_compra_status on lista_compra (status);
