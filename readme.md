# Sicredi - Servidor de Enquetes
Criado para avaliação técnica do Sicredi

# Change Log

## [Unreleased]
###Added
###Changed
- Endpoint de Contabilização do resultado passa a fazer a mesma coisa que o de Carrega resultado.
###Deprecated
###Removed
###Fixed
###Security

## [1.2.0] - 2023-07-10
###Changed
- Melhora de performance: resultado da votação passa a ser persistido no banco e não precisa ser recalculado toda vez.
###Deprecated
- Contabilização do resultado. No lugar deve ser utilizado: Contabiliza votos e Carrega resultado.

## [1.1.0] - 2023-07-07
###Added
- Cadastro e carregamento de pauta
- Abertura de sessão de votação
- Recebimento de votos dos associados em pautas
- Contabilização do resultado

## [1.0.0] - 2023-07-04
Primeira versão. Apenas criação do projeto.