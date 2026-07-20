create table parametro (
    id              bigint generated always as identity,
    chave           varchar(40)  not null,
    valor           numeric(9, 4) not null,
    vigencia_inicio date         not null,
    descricao       varchar(200),
    criado_em       timestamptz  not null,
    criado_por      varchar(100) not null,
    atualizado_em   timestamptz,
    atualizado_por  varchar(100),
    constraint pk_parametro primary key (id)
);

create index ix_parametro_chave_vigencia on parametro (chave, vigencia_inicio);
