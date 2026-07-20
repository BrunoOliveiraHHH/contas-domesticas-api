create table lancamento (
    id                 bigint generated always as identity,
    tipo               varchar(10)  not null,
    descricao          varchar(200) not null,
    valor              numeric(15, 2) not null,
    data_competencia   date         not null,
    data_vencimento    date,
    data_pagamento     date,
    status             varchar(15),
    carteira_id        bigint       not null,
    categoria_id       bigint       not null,
    forma_pagamento_id bigint,
    observacao         varchar(300),
    anexo_url          varchar(300),
    criado_em          timestamptz  not null,
    criado_por         varchar(100) not null,
    atualizado_em      timestamptz,
    atualizado_por     varchar(100),
    constraint pk_lancamento primary key (id),
    constraint fk_lancamento_carteira foreign key (carteira_id) references carteira (id),
    constraint fk_lancamento_categoria foreign key (categoria_id) references categoria (id),
    constraint fk_lancamento_forma_pagamento foreign key (forma_pagamento_id) references forma_pagamento (id)
);

create index ix_lancamento_competencia on lancamento (data_competencia);
create index ix_lancamento_carteira on lancamento (carteira_id);
create index ix_lancamento_tipo_status on lancamento (tipo, status);
