create table mercado (
    id             bigint generated always as identity,
    nome           varchar(150) not null,
    tipo           varchar(20)  not null,
    endereco       varchar(200),
    bairro         varchar(100),
    ativo          boolean      not null default true,
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_mercado primary key (id)
);

create index ix_mercado_tipo on mercado (tipo);
