create table investimento (
    id                bigint generated always as identity,
    nome              varchar(150) not null,
    tipo_investimento varchar(25)  not null,
    instituicao       varchar(120),
    carteira_id       bigint       not null,
    indexador         varchar(10),
    taxa_contratada   numeric(9, 4),
    data_aplicacao    date         not null,
    data_vencimento   date,
    criado_em         timestamptz  not null,
    criado_por        varchar(100) not null,
    atualizado_em     timestamptz,
    atualizado_por    varchar(100),
    constraint pk_investimento primary key (id),
    constraint fk_investimento_carteira foreign key (carteira_id) references carteira (id)
);

create index ix_investimento_tipo on investimento (tipo_investimento);
