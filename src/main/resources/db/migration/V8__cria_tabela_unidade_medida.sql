create table unidade_medida (
    id              bigint generated always as identity,
    nome            varchar(60)  not null,
    sigla           varchar(10)  not null,
    tipo            varchar(20)  not null,
    fator_para_base numeric(12, 6) not null default 1,
    criado_em       timestamptz  not null,
    criado_por      varchar(100) not null,
    atualizado_em   timestamptz,
    atualizado_por  varchar(100),
    constraint pk_unidade_medida primary key (id),
    constraint uk_unidade_medida_sigla unique (sigla)
);
