// ESPERA O HTML CARREGAR ANTES DE EXECUTAR QUALQUER COISA
window.addEventListener('DOMContentLoaded', () => {

    // ===============================================
    // --- 1. PEGAR TODOS OS ELEMENTOS
    // ===============================================

    // --- Telas Principais
    const telaLogin = document.getElementById('tela-login');
    const telaCadastro = document.getElementById('tela-cadastro');
    const telaApp = document.getElementById('tela-app');
    
    // --- Botões (Login e Cadastro)
    const btnIrParaCadastro = document.getElementById('btn-ir-para-cadastro');
    const btnEntrarApp = document.getElementById('btn-entrar-app');
    const btnIrParaLogin = document.getElementById('btn-ir-para-login');
    const btnEnviarCadastro = document.getElementById('btn-enviar-cadastro');
    
    // --- Inputs de Cadastro (para a máscara e limpeza)
    const cadEmailInput = document.getElementById('cad-email');
    const cadNomeInput = document.getElementById('cad-nome');
    const cadSenhaInput = document.getElementById('cad-senha');
    const cadConfirmaSenhaInput = document.getElementById('cad-confirma-senha');
    const cadNascimentoInput = document.getElementById('cad-nascimento');

    // --- Telas Internas do App
    const btnVoltarHome = document.getElementById('btn-voltar-home');
    const telaAtualizacoes = document.getElementById('tela-atualizacoes');
    const telaAmigos = document.getElementById('tela-amigos');
    const telaAtualizacoesAmigos = document.getElementById('tela-atualizacoes-amigos');
    const btnAbrirAmigos = document.getElementById('btn-abrir-amigos');
    
    // --- Menu de Perfil e Modal de Edição
    const btnMenuPerfil = document.getElementById('btn-menu-perfil');
    const menuPerfil = document.getElementById('menu-perfil');
    const btnAtualizarFoto = document.getElementById('btn-atualizar-foto');
    const btnAtualizarDados = document.getElementById('btn-atualizar-dados');
    const inputFotoPerfil = document.getElementById('input-foto-perfil');
    const modalEditarPerfil = document.getElementById('modal-editar-perfil');
    const btnFecharModal = document.getElementById('btn-fechar-modal');
    const btnSalvarDados = document.getElementById('btn-salvar-dados');
    
    // --- Configurações & Apagar Conta
    const btnAbrirConfig = document.getElementById('btn-abrir-config');
    const telaConfiguracoes = document.getElementById('tela-configuracoes');
    const btnAbrirModalApagar = document.getElementById('btn-abrir-modal-apagar');
    const modalApagarConta = document.getElementById('modal-apagar-conta');
    const btnFecharModalApagar = document.getElementById('btn-fechar-modal-apagar');
    const btnConfirmarApagar = document.getElementById('btn-confirmar-apagar');

    // --- Toggles de Configuração
    const togglePrivacyInput = document.getElementById('toggle-privacy');
    const privacyToggleLabel = document.querySelector('.privacy-toggle .privacy-text');
    const privacyToggleIcon = document.querySelector('.privacy-toggle .icon-lock');

    
    // ===============================================
    // --- 2. FUNÇÕES DE AJUDA (Helpers)
    // ===============================================
    
    /**
     * Converte uma data de "DD/MM/AAAA" para "AAAA-MM-DD".
     * Retorna null se o formato for inválido.
     */
    function formatarDataParaAPI(data) {
        // Espera "DD/MM/AAAA"
        const partes = data.split('/');
        if (partes.length !== 3 || partes[2].length < 4) {
            return null; // Formato inválido
        }
        // Retorna "AAAA-MM-DD"
        return `${partes[2]}-${partes[1]}-${partes[0]}`;
    }


    // ===============================================
    // --- 3. FUNÇÕES DE NAVEGAÇÃO E API
    // ===============================================

    // --- Navegação (Login / Cadastro)
    
    btnIrParaCadastro.addEventListener('click', () => {
        telaLogin.classList.add('escondido');
        telaCadastro.classList.remove('escondido');
    });
    
    btnIrParaLogin.addEventListener('click', () => {
        telaCadastro.classList.add('escondido');
        telaLogin.classList.remove('escondido');
    });

    // --- Máscara de Data (DD/MM/AAAA)
    cadNascimentoInput.addEventListener('input', (e) => {
        let valor = e.target.value.replace(/\D/g, ''); // Remove tudo que não for número

        if (valor.length > 4) {
            // DD/MM/AAAA
            valor = valor.slice(0, 2) + '/' + valor.slice(2, 4) + '/' + valor.slice(4, 8);
        } else if (valor.length > 2) {
            // DD/MM
            valor = valor.slice(0, 2) + '/' + valor.slice(2, 4);
        }
        e.target.value = valor;
    });

    // --- API: Botão ENVIAR CADASTRO
    btnEnviarCadastro.addEventListener('click', () => {
        // 1. Coletar os dados do formulário
        const email = cadEmailInput.value;
        const nome = cadNomeInput.value;
        const senha = cadSenhaInput.value;
        const senhaConfirmacao = cadConfirmaSenhaInput.value;
        const dataNascimentoInput = cadNascimentoInput.value;

        if (!email || !nome || !senha || !senhaConfirmacao || !dataNascimentoInput) {
            alert("Por favor, preencha todos os campos.");
            return;
        }

        if (senha !== senhaConfirmacao) {
            alert("As senhas não conferem!");
            return;
        }

        // 2. Formatar a data para o back-end
        const dataNascimentoFormatada = formatarDataParaAPI(dataNascimentoInput);

        if (!dataNascimentoFormatada) {
            alert("Formato de data inválido. Por favor, use DD/MM/AAAA.");
            return;
        }

        // 3. Criar o objeto (JSON) para enviar
        const dadosCadastro = {
            nome: nome,
            email: email,
            senha: senha,
            dataNascimento: dataNascimentoFormatada
        };

        // 4. Chamar a API com FETCH
        fetch('/api/usuarios/cadastro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosCadastro)
        })
        .then(response => {
            if (!response.ok) {
                // Tenta ler o erro que o back-end enviou
                return response.text().then(text => { throw new Error('Erro ao cadastrar: ' + text) });
            }
            return response.json(); 
        })
        .then(usuarioSalvo => {
            // 5. Sucesso!
            alert('Usuário cadastrado com sucesso! ID: ' + usuarioSalvo.id);

            // Limpar os campos
            cadEmailInput.value = '';
            cadNomeInput.value = '';
            cadSenhaInput.value = '';
            cadConfirmaSenhaInput.value = '';
            cadNascimentoInput.value = '';
            
            // Navega para a tela de login
            telaCadastro.classList.add('escondido');
            telaLogin.classList.remove('escondido');
        })
        .catch(error => {
            // 6. Falha
            console.error(error);
            alert(error.message);
        });
    });

    // --- API: Botão ENTRAR (Login)
    btnEntrarApp.addEventListener('click', () => {
        const email = document.getElementById('login-email').value;
        const senha = document.getElementById('login-senha').value;
    
        if (email === "" || senha === "") {
            alert('Por favor, digite seu e-mail e senha.');
            return;
        }
    
        // 1. Criar o objeto DTO para enviar
        const dadosLogin = {
            email: email,
            senha: senha
        };
    
        // 2. Chamar a API de Login
        fetch('/api/usuarios/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosLogin)
        })
        .then(response => {
            if (response.status === 401) { // Unauthorized
                throw new Error('E-mail ou senha inválidos.');
            }
            if (!response.ok) {
                throw new Error('Erro ao tentar fazer login.');
            }
            return response.json(); // Pega os dados do usuário
        })
        .then(usuarioLogado => {
            // 3. SUCESSO!
            alert('Login bem-sucedido! Bem-vindo(a), ' + usuarioLogado.nome);
        
            // (No futuro, você vai salvar o Token JWT aqui)
        
            // Limpa os campos de login
            document.getElementById('login-email').value = '';
            document.getElementById('login-senha').value = '';
        
            // 4. Troca de tela
            telaLogin.classList.add('escondido');
            telaCadastro.classList.add('escondido');
            telaApp.classList.remove('escondido');
        
            // (BÔNUS: Popular os dados do usuário na tela principal)
            document.querySelector('.profile-user-info').textContent = usuarioLogado.nome;
            // (Você pode adicionar um campo 'bio' no Usuario.java e popular aqui também)
            // document.querySelector('.profile-bio-box').textContent = usuarioLogado.bio;
        })
        .catch(error => {
            // 5. FALHA
            alert(error.message);
        });
    });
    
    
    // --- Navegação interna do App
    btnVoltarHome?.addEventListener('click', () => {
        telaAtualizacoesAmigos.classList.remove('escondido');
        telaAtualizacoes.classList.add('escondido');
        telaAmigos.classList.add('escondido');
        telaConfiguracoes.classList.add('escondido');
    });
    
    btnAbrirConfig?.addEventListener('click', () => {
        telaConfiguracoes.classList.remove('escondido');
        telaAtualizacoes.classList.add('escondido');
        telaAmigos.classList.add('escondido');
        telaAtualizacoesAmigos.classList.add('escondido');
    });
    
    btnAbrirAmigos?.addEventListener('click', () => {
        telaAmigos.classList.remove('escondido');
        telaAtualizacoes.classList.add('escondido');
        telaAtualizacoesAmigos.classList.add('escondido');
        telaConfiguracoes.classList.add('escondido');
    });
    
    // --- Menu de Perfil e Modal
    btnMenuPerfil?.addEventListener('click', (e) => {
        e.stopPropagation(); // Impede o clique de fechar o menu imediatamente
        menuPerfil.classList.toggle('escondido');
    });
    
    // Fecha o menu se clicar em qualquer outro lugar
    document.addEventListener('click', () => {
        if(menuPerfil) menuPerfil.classList.add('escondido');
    });
    
    btnAtualizarFoto.addEventListener('click', () => {
        menuPerfil.classList.add('escondido');
        inputFotoPerfil.click();
    });
    
    inputFotoPerfil.addEventListener('change', (e) => {
        if (e.target.files && e.target.files[0]) {
            const foto = e.target.files[0];
            alert('Nova foto selecionada: ' + foto.name);
        }
    });
    
    btnAtualizarDados.addEventListener('click', () => {
        menuPerfil.classList.add('escondido');
        modalEditarPerfil.classList.remove('escondido');
    });
    
    btnFecharModal.addEventListener('click', () => {
        modalEditarPerfil.classList.add('escondido');
    });
    
    btnSalvarDados.addEventListener('click', () => {
        alert('Dados enviados para atualização! (fictício)');
        modalEditarPerfil.classList.add('escondido');
    });
    
    // --- Configurações
    togglePrivacyInput.addEventListener('change', () => {
        if (togglePrivacyInput.checked) {
            privacyToggleLabel.textContent = 'PÚBLICO';
            privacyToggleIcon.textContent = '🔓';
        } else {
            privacyToggleLabel.textContent = 'PRIVADO';
            privacyToggleIcon.textContent = '🔒';
        }
    });
    
    btnAbrirModalApagar.addEventListener('click', () => {
        modalApagarConta.classList.remove('escondido');
    });
    
    btnFecharModalApagar.addEventListener('click', () => {
        modalApagarConta.classList.add('escondido');
    });
    
    btnConfirmarApagar.addEventListener('click', () => {
        modalApagarConta.classList.add('escondido');
        alert('Conta apagada com sucesso! (Fictício)');
        
        // Desloga o usuário
        telaApp.classList.add('escondido');
        telaLogin.classList.remove('escondido');
    });

}); // Fim do 'DOMContentLoaded'