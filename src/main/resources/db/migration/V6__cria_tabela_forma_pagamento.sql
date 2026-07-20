create table forma_pagamento (
    id             bigint generated always as identity,
    nome           varchar(120) not null,
    tipo           varchar(20)  not null,
    carteira_id    bigint,
    dia_fechamento smallint,
    dia_vencimento smallint,
    ativa          boolean      not null default true,
    criado_em      timestamptz  not null,
    criado_por     varchar(100) not null,
    atualizado_em  timestamptz,
    atualizado_por varchar(100),
    constraint pk_forma_pagamento primary key (id),
    constraint fk_forma_pagamento_carteira foreign key (carteira_id) references carteira (id)
);
