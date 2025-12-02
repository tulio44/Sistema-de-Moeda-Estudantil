package com.moedaestudantil.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moedaestudantil.domain.model.NotificacaoEmail;
import com.moedaestudantil.domain.repo.NotificacaoEmailRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificacaoEmailService {

    private final NotificacaoEmailRepository repo;
    private final EmailService emailService;
    private final ObjectMapper mapper = new ObjectMapper();

    public NotificacaoEmailService(NotificacaoEmailRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 8000)
    public void processar() {

        List<NotificacaoEmail> pendentes =
                repo.findAll().stream()
                        .filter(n -> n.getStatus().equals("PENDENTE"))
                        .toList();

        for (NotificacaoEmail n : pendentes) {

            String detalhes = formatarPayload(n.getPayloadJson());

            String html = """
<html>
  <body style="margin:0;padding:0;background:#f4f4f7;font-family:Arial, sans-serif;">
    <div style="max-width:600px;margin:30px auto;background:#ffffff;border-radius:8px;overflow:hidden;
                box-shadow:0 2px 10px rgba(0,0,0,0.08);">

      <div style="background:#4F46E5;padding:20px;text-align:center;color:white;">
        <h2 style="margin:0;font-size:22px;font-weight:600;">Moeda Estudantil</h2>
      </div>

      <div style="padding:25px 30px;color:#333;">
        <h3 style="margin-top:0;">%s</h3>

        <div style="background:#f9fafb;border-left:4px solid #4F46E5;padding:12px 15px;
                    border-radius:5px;font-size:15px;line-height:1.6;">
          %s
        </div>

        <p style="margin-top:25px;font-size:14px;color:#666;">
          Em caso de dúvidas, entre em contato com sua instituição.
        </p>
      </div>

      <div style="background:#f9fafb;padding:15px;text-align:center;color:#999;font-size:12px;">
        Sistema de Moeda Estudantil • 2025<br>
        Este é um email automático. Não responda.
      </div>
    </div>
  </body>
</html>
""".formatted(n.getTipo(), detalhes);

            try {
                emailService.enviarEmail(
                        n.getDestinatarioEmail(),
                        "Moeda Estudantil - " + n.getTipo(),
                        html
                );

                n.setStatus("ENVIADO");
                repo.save(n);

            } catch (Exception e) {
                System.out.println("❌ Falha ao enviar email: " + e.getMessage());
            }
        }
    }


    private String formatarPayload(String json) {
        try {
            Map<String, Object> map = mapper.readValue(json, Map.class);

            StringBuilder sb = new StringBuilder();

            for (var entry : map.entrySet()) {
                String chave = entry.getKey();
                String valor = String.valueOf(entry.getValue());

                // Destaque especial para cupom
                if (chave.equalsIgnoreCase("codigo")) {
                    sb.append("""
                        <div style="margin-top:10px;margin-bottom:10px;text-align:center;">
                            <strong style="color:#4F46E5;font-size:18px;">Código do Cupom:</strong><br>
                            <span style="font-size:28px;font-weight:bold;color:#111;">
                                %s
                            </span>
                        </div>
                    """.formatted(valor));
                    continue;
                }

                sb.append("""
                    <div style="margin-bottom:8px;">
                        <strong style="color:#333;">%s:</strong> %s
                    </div>
                """.formatted(capitalize(chave), valor));
            }

            return sb.toString();

        } catch (Exception e) {
            return "<pre>" + json + "</pre>";
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
