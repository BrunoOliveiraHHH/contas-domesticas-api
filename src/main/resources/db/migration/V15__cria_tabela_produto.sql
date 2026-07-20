create table produto (
    id                       bigint generated always as identity,
    nome                     varchar(150) not null,
    descricao                varchar(300),
    categoria_id             bigint,
    unidade_medida_padrao_id bigint,
    codigo_barras            varchar(60),
    ativo                    boolean      not null default true,
    criado_em                timestamptz  not null,
    criado_por               varchar(100) not null,
    atualizado_em            timestamptz,
    atualizado_por           varchar(100),
    constraint pk_produto primary key (id),
    constraint fk_produto_categoria foreign key (categoria_id) references categoria (id),
    constraint fk_produto_unidade_medida foreign key (unidade_medida_padrao_id) references unidade_medida (id)
);

create index ix_produto_nome on produto (nome);
